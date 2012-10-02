package org.hornetq.replicator.client;

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
			parameters.put("port", org.hornetq.core.remoting.impl.netty.TransportConstants.DEFAULT_PORT);
			
			
			TransportConfiguration configuration = new TransportConfiguration(NettyConnectorFactory.class.getName(), parameters);
			HornetQConnectionFactory factory = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, configuration);
			Connection conn = factory.createConnection();
			Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}
}
