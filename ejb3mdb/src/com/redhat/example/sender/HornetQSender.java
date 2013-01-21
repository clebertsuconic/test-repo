package com.redhat.example.sender;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.*;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;

public class HornetQSender {
	public static void main(String arg[])
	{
		
		try
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("host", "127.0.0.1");
			parameters.put("port", org.hornetq.core.remoting.impl.netty.TransportConstants.DEFAULT_PORT);
			
			
			TransportConfiguration configuration = new TransportConfiguration(NettyConnectorFactory.class.getName(), parameters);
			HornetQConnectionFactory factory = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, configuration);
			factory.setBlockOnDurableSend(false);
			factory.setBlockOnAcknowledge(false);
			Connection conn = factory.createConnection();
			Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			
			Topic topic = HornetQJMSClient.createTopic("topicA");
			
			MessageProducer prod = sess.createProducer(topic);
			
			for (int i = 0 ; i < 20; i++)
			{
				TextMessage msg = sess.createTextMessage();
				msg.setStringProperty("tst", "seq " + i);
				prod.send(msg);
				//Thread.sleep(500);
			}

			conn.close();
			factory.close();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		
		System.out.println("Finished test");
		
		
	}
}
