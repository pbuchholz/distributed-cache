package distributedcache.cache.configuration.boundary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import distributedcache.cache.notification.Notification;

/**
 * Deserializes {@link Notification}s to be received from Kafka. Currently uses
 * Java Serialization.
 * 
 * @author Philipp Buchholz
 */
public class NotificationDeserializer implements Deserializer<Notification<?>> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public Notification<?> deserialize(String topic, byte[] data) {
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {

			Notification<?> notification = (Notification<?>) ois.readObject();
			return notification;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {

	}

}
