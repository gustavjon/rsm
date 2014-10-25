package gustav.rsm;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

class JmsMessageListener implements MessageListener {

	private Callback callback;

	public JmsMessageListener(Callback route) {

		callback = route;
	}

	@Override
	public void onMessage(Message message) {
		try {

			if (message instanceof TextMessage) {
				TextMessage textmsg = (TextMessage) message;

				String text = textmsg.getText();
				callback.handle(text);
			}
			else{
				System.out.print("WRONG MESSAGE TYPE");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
