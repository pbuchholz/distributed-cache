package distributedcache.cache.notification.kafka;

import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Component;

import distributedcache.cache.notification.Notification;
import distributedcache.cache.notification.NotificationListener;
import distributedcache.cache.notification.NotificationSubscriber;

/**
 * Implementation of {@link NotificationSubscriber} which listens for
 * {@link Notification}s on Apache Kafka.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
@Component
public class KafkaNotificationSubscriber<K, T>
		implements NotificationSubscriber<KafkaSubscription<K, Notification<T>>, T> {

	@Autowired
	private SchedulingTaskExecutor schedulingTaskExecutor;

	@Override
	public void subscribe(KafkaSubscription<K, Notification<T>> subscription, NotificationListener<T> listener) {

		schedulingTaskExecutor.submit(new Runnable() {

			@Override
			public void run() {
				Consumer<K, Notification<T>> notificationConsumer = subscription.getConsumer();
				notificationConsumer.subscribe(Arrays.asList(subscription.inTopic().getTopicName()));
				while (true) {
					try {
						ConsumerRecords<K, Notification<T>> consumerRecords = notificationConsumer
								.poll(Duration.ofMillis(100));
						consumerRecords.forEach(r -> listener.onNotification(r.value()));
						notificationConsumer.commitSync();

						// TODO ack

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

	}

}
