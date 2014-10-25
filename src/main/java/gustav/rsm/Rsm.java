package gustav.rsm;

public final class Rsm {

	private static boolean initialized = false;
	private static String brokerUrl;
	private static JmsManager jmsManager;

	private Rsm() {
	}

	public static synchronized void brokerUrl(String url) {
		brokerUrl = url;
	}

	static void init() {
		if (!initialized) {
			jmsManager = new JmsManager(brokerUrl);
			initialized = true;
		}
	}

	public static synchronized void send(String destination, String message) {
		init();
		jmsManager.sendOnQueue(destination, message);
	}

	public static synchronized void listen(String destination, Callback callback) {
		init();
		jmsManager.setupQueueListener(destination, callback);

	}

	public static synchronized void broadcast(String destination, String message) {
		init();
		jmsManager.sendOnTopic(destination, message);
	}

	public static void subscribe(String destination, Callback callback) {
		init();
		jmsManager.setupTopicListener(destination, callback);
	}

}
