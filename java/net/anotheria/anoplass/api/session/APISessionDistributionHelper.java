package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.SessionAttribute;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorService;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
import net.anotheria.net.util.ByteArraySerializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class APISessionDistributionHelper {
	private static SessionDistributorService distributorService;
	private static Logger log = Logger.getLogger(APISessionDistributionHelper.class);
	
	public static void setSessionDistributorService(SessionDistributorService aSessionDistributorService){
		distributorService = aSessionDistributorService;
	}

	/**
	 * Return true if sessionDistribution service was assigned to current Helper,
	 * false otherwise.
	 * @return boolean value
	 *
	 */
	public static boolean isSessionDistributorHelperConfigured(){
		return distributorService!=null;
	}
	
	/**
	 * Distributes the session. Returns the name of the distributed session.
	 * @param session
	 * @return
	 */
	public static String distributeSession(APISession session) throws APISessionDistributionException{
		if (distributorService==null)
			throw new IllegalStateException("No SessionDistributorService configured. Please set a SessionDistributorService.");
		if (!(session instanceof APISessionImpl))
			throw new AssertionError("Unknown APISession implementation");
		List<SessionAttribute> attributes = createAttributeList(session);
		try{
			return distributorService.createDistributedSession(attributes);
		}catch(SessionDistributorServiceException e){
			throw new APISessionDistributionException(e);
		}
	}
	
	/**
	 * Restores previously distributed session.
	 * @param distributedSessionName the name of the distributed session.
	 * @param session session to distribute into.
	 * @throws APISessionDistributionException
	 */
	public static void restoreSession(String distributedSessionName, APISession session) throws APISessionDistributionException{
		if (distributorService==null)
			throw new IllegalStateException("No SessionDistributorService configured. Please set a SessionDistributorService.");
		if (!(session instanceof APISessionImpl))
			throw new AssertionError("Unknown APISession implementation");
		List<SessionAttribute> attributes = null;
		try{
			attributes = distributorService.getAndDeleteDistributedSession(distributedSessionName);
		}catch(SessionDistributorServiceException e){
			throw new APISessionDistributionException(e);
		}
		
		APISessionImpl sessionImpl = (APISessionImpl) session;
		sessionImpl.setCurrentUserId(byteArrayToString(attributes.get(0).getData()));
		sessionImpl.setCurrentEditorId(byteArrayToString(attributes.get(1).getData()));
		attributes = attributes.subList(2, attributes.size());
		
		for (SessionAttribute attribute : attributes){
			try{
				AttributeWrapper wrapper = (AttributeWrapper)ByteArraySerializer.deserializeObject(attribute.getData());
				sessionImpl.setAttributeWrapper(wrapper);
			}catch(IOException e){
				log.error("exception occured attempting to deserialize this attribute: "+attribute, e);
			}
		}
		
	}
	
	private static byte[] stringToByteArray(String s){
		return s == null ? null : s.getBytes();
	}
	
	private static String byteArrayToString(byte[] data){
		return data == null ? null : new String(data);
	}

	private static List<SessionAttribute> createAttributeList(APISession session){
		Collection<AttributeWrapper> attributes = ((APISessionImpl)session).getAttributes();
		ArrayList<SessionAttribute> ret = new ArrayList<SessionAttribute>();
		
		//add session internal attributes
		SessionAttribute userId = new SessionAttribute("userId", stringToByteArray(session.getCurrentUserId()));
		SessionAttribute editorId = new SessionAttribute("editorId", stringToByteArray(session.getCurrentEditorId()));
		ret.add(userId);
		ret.add(editorId);
		
		for (AttributeWrapper wrapper : attributes){
			if (PolicyHelper.isDistributed(wrapper.getPolicy())){
				if (wrapper.isSerializable()){
					try{
						byte[] data = ByteArraySerializer.serializeObject(wrapper);
						ret.add(new SessionAttribute(wrapper.getKey(), data));
					}catch(IOException e){
						log.error("exception occured attempting to serialize this attribute: "+wrapper, e);
					}
				}else{
					log.warn("Attribute "+wrapper.getKey()+" is marked as distributed but is not serializeable, skipped.");
				}
			}
		}
		return ret;
	}
	
	private APISessionDistributionHelper(){}
}
