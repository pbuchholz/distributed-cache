package distributedcache.cache.notification.kafka;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import distributedcache.ApplicationConfiguration;
import distributedcache.cache.configuration.boundary.Consume;
import distributedcache.cache.configuration.boundary.ConsumerGroup;
import distributedcache.cache.configuration.boundary.KeyDeserializer;
import distributedcache.cache.configuration.boundary.KeySerializer;
import distributedcache.cache.configuration.boundary.ValueDeserializer;
import distributedcache.cache.configuration.boundary.ValueSerializer;
import distributedcache.cache.notification.Notification;

/**
 * Creates {@link KafkaSubscription}s.
 * 
 * @author Philipp Buchholz
 */
@Dependent
public class KafkaSubscriptionFactory<K> {

	@Inject
	@KeySerializer
	@ValueSerializer("distributedcache.configuration.boundary.NotificationSerializer")
	private Producer<Long, Notification<K>> notificationProducer;

	@Inject
	@Consume
	@ConsumerGroup("cache.notifications")
	@KeyDeserializer
	@ValueDeserializer("distributedcache.configuration.boundary.NotificationDeserializer")
	private Consumer<Long, Notification<K>> notificationConsumer;

	@Inject
	private ApplicationConfiguration applicationConfiguration;

	@Produces
	public KafkaSubscription<Long, Notification<K>> createKafkaSubscription() {
		return KafkaSubscription.<Long, Notification<K>>builder() //
				.consumer(notificationConsumer) //
				.producer(notificationProducer) //
				.inTopic(Topic.builder() //
						.name(applicationConfiguration.getIn()) //
						.build()) //
				.outTopic(Topic.builder() //
						.name(applicationConfiguration.getOut()) //
						.build()) //
				.failTopic(Topic.builder() //
						.name(applicationConfiguration.getFail()) //
						.build()) //
				.build();
	}

}
