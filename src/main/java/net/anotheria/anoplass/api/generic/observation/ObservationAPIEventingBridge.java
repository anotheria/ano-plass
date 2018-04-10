package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.anoprise.eventservice.Event;
import net.anotheria.anoprise.eventservice.EventChannel;
import net.anotheria.anoprise.eventservice.EventService;
import net.anotheria.anoprise.eventservice.EventServiceFactory;
import net.anotheria.anoprise.eventservice.EventServicePushConsumer;
import net.anotheria.anoprise.eventservice.EventServicePushSupplier;
import net.anotheria.util.IdCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 10.04.18 16:57
 */
public class ObservationAPIEventingBridge {

	public static final String EVENT_CHANNEL_NAME = "api-observation-channel";
	private Consumer consumer;
	private Supplier supplier;
	static String originatorId = "ObservationAPIEventingBridge-"+ IdCodeGenerator.generateCode(10);

	private static Logger log = LoggerFactory.getLogger(ObservationAPIEventingBridge.class);

	private ObservationAPIImpl parent;

	ObservationAPIEventingBridge(ObservationAPIImpl parent){
		consumer = new Consumer(parent);
		supplier = new Supplier();

		EventService service = EventServiceFactory.createEventService();
		EventChannel forSupplier = service.obtainEventChannel(EVENT_CHANNEL_NAME, supplier);
		supplier.setChannel(forSupplier);

		EventChannel forConsumer = service.obtainEventChannel(EVENT_CHANNEL_NAME, consumer);
		forConsumer.addConsumer(consumer);

		log.info("ObservationAPIEventingBridge initialized with originatorId: "+originatorId);
	}

	public void fireSubjectUpdateForUser(String subject, String originator, String userId) {
		supplier.fireSubjectUpdateForUser(subject, originator, userId);
	}

	public class Consumer implements EventServicePushConsumer{

		private ObservationAPIImpl parent;

		public Consumer(ObservationAPIImpl parent) {
			this.parent = parent;
		}

		@Override
		public void push(Event event) {
			if (event.getOriginator()==null){
				log.debug("Received event without originator, skiping");
				return;
			}
			if (event.getOriginator().equals(originatorId)){
				log.debug("Received event sent by myself, droping");
				return;
			}

			EventData data = (EventData)event.getData();
			parent.remotelyFiredSubjectUpdateForUser(data.getSubject(), data.getOriginator(), data.getUserId());

		}

	}

	public class Supplier implements EventServicePushSupplier{
		private EventChannel channel;


		public void setChannel(EventChannel channel) {
			this.channel = channel;
		}

		public void fireSubjectUpdateForUser(String subject, String originator, String userId) {
			channel.push(new ObservationEvent(subject, originator, userId));
			
		}
	}

	public static class ObservationEvent extends Event implements Serializable{
		private static final long serialVersionUID = 1L;
		public ObservationEvent(String subject, String originator, String userId){
			super(originatorId, new EventData(subject, originator, userId));
		}
	}

	public static class EventData implements Serializable{

		private static final long serialVersionUID = 1L;

		private String subject;
		private String originator;
		private String userId;

		public EventData(String subject, String originator, String userId) {
			this.subject = subject;
			this.originator = originator;
			this.userId = userId;
		}

		public String getSubject() {
			return subject;
		}

		public String getOriginator() {
			return originator;
		}

		public String getUserId() {
			return userId;
		}

		@Override
		public String toString() {
			return "EventData{" +
					"subject='" + subject + '\'' +
					", originator='" + originator + '\'' +
					", userId='" + userId + '\'' +
					'}';
		}
	}

}
