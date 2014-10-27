package gustav.rsm;

import gustav.rsm.jms.JmsManager;

public final class Rsm {

	private static boolean initialized = false;
	private static String brokerUrl;
	private static JmsManager jmsManager;

	private Rsm() {
	}

	public static synchronized void brokerUrl(String url) {
		brokerUrl = url;
	}

	private static void init() {
		if (!initialized) {
			if (brokerUrl == null) {
				throw new RsmException("No broker URL set");
			}

			jmsManager = new JmsManager(brokerUrl);
			initialized = true;
		}
	}

	public static synchronized void send(Queue destination, String message) {
		init();
		jmsManager.sendOnQueue(destination, message);
	}

	public static synchronized void send(Queue destination, String message,
			Callback callback) {
		//TODO implement with temporary queue(?)
		init();
	}

	public static synchronized void send(Queue destination, String message,
			Topic responseDestination, Callback callback) {
		init();
	}

	public static synchronized void listen(Queue destination, Callback callback) {
		init();
		jmsManager.setupQueueListener(destination, callback);

	}

	public static synchronized void broadcast(Topic destination, String message) {
		init();
		jmsManager.sendOnTopic(destination, message);
	}

	public static void subscribe(Topic destination, Callback callback) {
		init();
		jmsManager.setupTopicListener(destination, callback);
	}

}
