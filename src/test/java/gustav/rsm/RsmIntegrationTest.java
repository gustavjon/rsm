package gustav.rsm;
import static org.junit.Assert.assertEquals;
import gustav.rsm.Rsm;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.broker.BrokerService;
import org.junit.BeforeClass;
import org.junit.Test;


public class RsmIntegrationTest {
	
	private static final String BROKER_URL = "vm://localhost";
	private static final String DESTINATION_QUEUE = "queue";
	private static final String DESTINATION_TOPIC = "topic";
	private static final String MSG = "Hej ";
	private static final String MSG2 = "Tjena ";
	private String queueMsg;
	private String topicMsg;
	private AtomicInteger recievedMessagesOnQueue = new AtomicInteger(0);
	private AtomicInteger recievedMessagesOnTopic = new AtomicInteger(0);

	private static BrokerService broker;
	
	@BeforeClass
	public static void setupBroker() throws Exception{
		broker = new BrokerService();
		broker.addConnector(BROKER_URL);		
		broker.start();
	}
	
	@Test
	public void sendMessageOnQueueTest() throws InterruptedException{
		Rsm.brokerUrl(BROKER_URL);
		Queue q = new Queue(DESTINATION_QUEUE);
		Rsm.listen(q, this::onQueueMessage);
		Rsm.listen(q, this::onQueueMessage);
		Rsm.send(q, MSG);
		Thread.sleep(200);
		assertEquals(MSG, queueMsg);
		assertEquals(1, recievedMessagesOnQueue.get());
	}
	
	@Test
	public void sendMessageOnTopicTest() throws InterruptedException{
		Rsm.brokerUrl(BROKER_URL);
		Topic t = new Topic(DESTINATION_TOPIC);
		Rsm.subscribe(t, this::onTopicMessage);
		Rsm.subscribe(t, this::onTopicMessage);
		Rsm.broadcast(t, MSG2);
		Thread.sleep(200);
		assertEquals(MSG2, topicMsg);
		assertEquals(2, recievedMessagesOnTopic.get());
	}
	
	@Test
	public void sendMessageOnTopicHandleResponseMultithreaded() throws InterruptedException{
		Rsm.brokerUrl(BROKER_URL);
		Topic t = new Topic(DESTINATION_TOPIC);
		Rsm.subscribe(t, this::onTopicMessageSleep200);
		Rsm.subscribe(t, this::onTopicMessageSleep200);
		Rsm.broadcast(t, MSG2);
		Thread.sleep(250);
		assertEquals(MSG2, topicMsg);
		assertEquals(2, recievedMessagesOnTopic.get());
	}
	
	
	private void onQueueMessage(String message){
		queueMsg = message;
		recievedMessagesOnQueue.incrementAndGet();
	}
	
	private void onTopicMessage(String message){
		topicMsg = message;
		recievedMessagesOnTopic.incrementAndGet();
	}
	
	private void onTopicMessageSleep200(String message) throws InterruptedException{
		topicMsg = message;
		recievedMessagesOnTopic.incrementAndGet();
		Thread.sleep(200);
	}
	
	

}
