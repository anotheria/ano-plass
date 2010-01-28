package net.anotheria.anoplass.api.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.anotheria.anoplass.api.session.APISessionManager;

import org.apache.log4j.Logger;

public class APISessionListener implements HttpSessionListener{

	private static Logger log = Logger.getLogger(APISessionListener.class);

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
