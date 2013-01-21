package com.redhat.example;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(messageListenerInterface = javax.jms.MessageListener.class, activationConfig = { 
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "/topic/topicA"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "test"),
    @ActivationConfigProperty(propertyName = "clientID", propertyValue = "test"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable")
})
public class ExampleEJB3MDB implements MessageListener {

    public void onMessage(Message m) {
    	try {
			System.out.println("Message " + m.getStringProperty("tst"));
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }
}
