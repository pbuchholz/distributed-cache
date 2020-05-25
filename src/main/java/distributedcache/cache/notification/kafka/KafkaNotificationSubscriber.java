package distributedcache.cache.notification.kafka;

import java.time.Duration;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import distributedcache.cache.notification.Notification;
import distributedcache.cache.notification.NotificationListener;
import distributedcache.cache.notification.NotificationSubscriber;

@Dependent
public class KafkaNotificationSubscriber<K, T>
		implements NotificationSubscriber<KafkaSubscription<K, Notification<T>>, T> {

	@Resource
	private ManagedExecutorService managedExecutorService;

	@Override
	public void subscribe(KafkaSubscription<K, Notification<T>> subscription, NotificationListener<T> listener) {
		managedExecutorService.submit(new Runnable() {

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
						e.printStackTrace();
						// Produce to fail

						
					}
				}
			}
		});

	}

}
