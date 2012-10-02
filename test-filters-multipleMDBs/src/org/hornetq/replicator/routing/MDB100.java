package org.hornetq.replicator.routing;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven
(
    activationConfig =
    {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "MDB100"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/testTopic"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15"),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "receiver=100") })
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class MDB100 implements MessageListener {
	private static final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public void onMessage(Message msg) {
		int value = counter.incrementAndGet();
		if (value % 100 == 0)
		{
			System.out.println("Received " + value + " on " + this.getClass().getSimpleName());
		}
	}
}
