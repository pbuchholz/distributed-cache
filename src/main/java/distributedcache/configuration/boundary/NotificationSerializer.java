package distributedcache.configuration.boundary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import distributedcache.notification.Notification;

/**
 * Serializes {@link Notification}s to be send over Kafka.
 * 
 * @author Philipp Buchholz
 */
public class NotificationSerializer implements Serializer<Notification> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public byte[] serialize(String topic, Notification data) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
			objectOutputStream.writeObject(data);
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
	}

}
