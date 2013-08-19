package net.anotheria.anoplass.api.listener;

import net.anotheria.anoplass.api.session.APISessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


public class APISessionListener implements HttpSessionListener{

    /**
     * {@link Logger} logger.
     */
	private static Logger log = LoggerFactory.getLogger(APISessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
		
	}

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
