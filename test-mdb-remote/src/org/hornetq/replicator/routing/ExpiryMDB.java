package org.hornetq.replicator.routing;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/ExpiryQueue"), })
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
public class ExpiryMDB implements MessageListener {

	@Override
	public void onMessage(Message msg) {
		Logger.getLogger(ExpiryMDB.class.getName()).log(Level.INFO,	"!!!!Message expired");

		StringBuffer msgInfo = new StringBuffer();
		try {
			Enumeration<?> properiesNames = msg.getPropertyNames();
			while (properiesNames.hasMoreElements()) {
				String name = properiesNames.nextElement().toString();
				msgInfo.append("\n" + name + " = " + msg.getObjectProperty(name));
			}
			Logger.getLogger(ExpiryMDB.class.getName()).log(Level.SEVERE, "!!!!Message expired: " + msgInfo.toString());
		} catch (Exception e) {
			Logger.getLogger(ExpiryMDB.class.getName()).log(Level.SEVERE, "!!!!Message expired, failed to get properties", e);
		}
	}
}
