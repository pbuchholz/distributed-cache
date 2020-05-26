package distributedcache.cache.notification.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.Dependent;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import distributedcache.cache.notification.Notification;
import distributedcache.cache.notification.NotificationPublisher;

/**
 * Implementation of {@link NotificationPublisher} which sends
 * {@link Notification}s through Kafka.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
@Dependent
public class KafkaNotificationPublisher<K, T>
		implements NotificationPublisher<KafkaSubscription<K, Notification<T>>, T> {

	@Override
	public void publish(KafkaSubscription<K, Notification<T>> subscription, Notification<T> notification) {
		try {
			/* Dont publish in case its an in only configuration. */
			if (!subscription.outTopic().isPresent()) {
				return;
			}

			Producer<K, Notification<T>> notificationProducer = subscription.getProducer();

			Future<RecordMetadata> sendFuture = notificationProducer
					.send(new ProducerRecord<>(subscription.outTopic().getTopicName(), notification));

			/* Wait till result is received. */
			sendFuture.get();
			if (!sendFuture.isDone()) {
				throw new RuntimeException(
						String.format("Notification %1 could not be send.", notification.toString()));

			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
