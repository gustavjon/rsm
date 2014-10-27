package gustav.rsm.jms;

import gustav.rsm.Callback;
import gustav.rsm.Queue;
import gustav.rsm.RsmDestination;
import gustav.rsm.Topic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private Map<RsmDestination, JmsData> destinationRoutingMap= new ConcurrentHashMap<>();
	
	public JmsManager(String brokerUrl) {
		startConnection(brokerUrl);
	}

	private void startConnection(String brokerUrl) {
		// Create a ConnectionFactory
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					brokerUrl);

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
	}

	public void setupTopicListener(Topic topic, Callback callback) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = session.createTopic(topic.name());
			addListener(session, dest, callback);

		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	public void setupQueueListener(Queue queue, Callback callback) {
		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = session.createQueue(queue.name());
			addListener(session, dest, callback);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	public void sendOnQueue(Queue queue, String msg) {
		try {
			if(!destinationRoutingMap.containsKey(queue)){
				Session session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue(queue.name());
				destinationRoutingMap.put(queue, new JmsData(session, destination));
			}
			sendMessage(destinationRoutingMap.get(queue), msg);

		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}



	public void sendOnTopic(Topic topicId, String msg) {
		try {
			if(!destinationRoutingMap.containsKey(topicId)){
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic(topicId.name());
			destinationRoutingMap.put(topicId, new JmsData(session, destination));
			}
			sendMessage(destinationRoutingMap.get(topicId), msg);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	private void addListener(Session session, Destination dest,
			Callback callback) throws JMSException {
		MessageConsumer consumer = session.createConsumer(dest);
		consumer.setMessageListener(new JmsMessageListener(callback));
	}

	private void sendMessage(JmsData jmsData, String msg) throws JMSException {
		sendMessage(jmsData.getSession(), jmsData.getDestination(), msg);
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
