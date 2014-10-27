package gustav.rsm.jms;

import javax.jms.Destination;
import javax.jms.Session;

public class JmsData {
	
	
	private Session session;
	private Destination destination;

	public JmsData(Session session, Destination dest){
		this.session = session;
		this.destination = dest;
	}

	public Session getSession() {
		return session;
	}

	public Destination getDestination() {
		return destination;
	}
}
