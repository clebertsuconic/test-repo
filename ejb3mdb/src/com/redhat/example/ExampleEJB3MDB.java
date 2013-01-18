package com.redhat.example;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.logging.Logger;

@MessageDriven(messageListenerInterface = javax.jms.MessageListener.class, activationConfig = { 
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "/queue/A")})
public class ExampleEJB3MDB implements MessageListener {
    private static final Logger logger = Logger.getLogger(ExampleEJB3MDB.class);

    public void onMessage(Message m) {
        logger.info("Got a message from \"queue/A\"");
    }
}
