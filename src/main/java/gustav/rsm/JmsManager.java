package gustav.rsm;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsManager {

	private Connection connection;

	public JmsManager(String brokerUrl) {
		startConnection(brokerUrl);
	}

	private void startConnection(String brokerUrl) {
		// Create a ConnectionFactory
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"vm://localhost");

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
	}

	public void setupTopicListener(String topicId, Callback callback) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = session.createTopic(topicId);
			addListener(session, dest, callback);

		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	public void setupQueueListener(String queueId, Callback callback) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = session.createQueue(queueId);
			addListener(session, dest, callback);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	public void sendOnQueue(String queueId, String msg) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueId);
			sendMessage(session, destination, msg);

		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendOnTopic(String topicId, String msg) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic(topicId);
			sendMessage(session, destination, msg);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	private void addListener(Session session, Destination dest,
			Callback callback) throws JMSException {
		MessageConsumer consumer = session.createConsumer(dest);
		consumer.setMessageListener(new JmsMessageListener(callback));
	}

	private void sendMessage(Session session, Destination dest, String msg)
			throws JMSException {
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		TextMessage message = session.createTextMessage(msg);
		producer.send(message);
		session.close();
	}

}
