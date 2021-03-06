package org.hornetq.replicator.routing;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

@MessageDriven
(
    activationConfig =
    {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "MDB21"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/testTopic"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15"),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "receiver=21") })
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class MDB21 implements MessageListener {
	private static final AtomicInteger counter = new AtomicInteger(0);

	private @Resource(mappedName = "java:/JmsXA")
	ConnectionFactory connectionFactory;

	private @Resource(mappedName = "java:/topic/testTopic")
	Topic topic;
	
	

	private @Resource MessageDrivenContext sessionContext;

	@Override
	public void onMessage(Message msg) {
		
		MDBUtil.msgReceived(msg, counter, connectionFactory, sessionContext, topic, getClass());
		
	}
}
