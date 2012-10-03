package org.hornetq.replicator.routing;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class MDBUtil {
	
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static long randomLong()
	{
		long value = random.nextLong();
		
		if (value < 0) return -value;
		else return value;
	}
	
	public static void msgReceived(AtomicInteger counter, ConnectionFactory connectionFactory, MessageDrivenContext sessionContext, Topic topic, Class clazz) {
		int value = counter.incrementAndGet();
		if (value % 100 == 0)
		{
			System.out.println("Received " + value + " on " + clazz.getSimpleName());
		}
		
		try {
			if (value < 20)
			{
				System.out.println(clazz.getSimpleName() + " waiting 1 second on " + value);
				Thread.sleep(1000);
			}
			else
			{
				// Thread.sleep(randomLong() % 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Connection conn = connectionFactory.createConnection();
			Session sess = conn.createSession(true, Session.SESSION_TRANSACTED);
			MessageProducer prod = sess.createProducer(topic);
			TextMessage outMessage = sess.createTextMessage("hello");
			outMessage.setIntProperty("receiver", 100);
			prod.send(outMessage);
			conn.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sessionContext.setRollbackOnly();
		}
	}

}
