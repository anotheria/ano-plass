package net.anotheria.anoplass.api.listener;

import net.anotheria.anoplass.api.session.APISessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;


/**
 * <p>APISessionListener class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class APISessionListener implements HttpSessionListener{

    /**
     * {@link Logger} logger.
     */
	private static Logger log = LoggerFactory.getLogger(APISessionListener.class);

	/** {@inheritDoc} */
	public void sessionCreated(HttpSessionEvent event) {
		
	}

	/** {@inheritDoc} */
	public void sessionDestroyed(HttpSessionEvent event) {
		try{
			APISessionManager.getInstance().destroyAPISessionByReferenceId(event.getSession().getId());
		}catch(Exception e){
			if (log!=null)
				log.error("APISessionManager.destroyAPISessionByReferenceId failed:", e);
			else
				System.err.println(this.getClass()+" log is null!");
		}
	}

}
