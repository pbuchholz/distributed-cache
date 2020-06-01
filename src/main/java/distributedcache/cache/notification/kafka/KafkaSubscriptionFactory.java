package distributedcache.cache.notification.kafka;

import javax.inject.Inject;
import javax.ws.rs.Produces;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class KafkaSubscriptionFactory<K> {

	@Autowired
	@KeySerializer
	@ValueSerializer("distributedcache.cache.configuration.boundary.NotificationSerializer")
	private Producer<Long, Notification<K>> notificationProducer;

	@Autowired
	@Consume
	@ConsumerGroup("cache.notifications")
	@KeyDeserializer
	@ValueDeserializer("distributedcache.cache.configuration.boundary.NotificationDeserializer")
	private Consumer<Long, Notification<K>> notificationConsumer;

	@Inject
	private ApplicationConfiguration applicationConfiguration;

	@Produces
	public KafkaSubscription<Long, Notification<K>> createKafkaSubscription() {
		return KafkaSubscription.<Long, Notification<K>>builder() //
				.consumer(notificationConsumer) //
				.producer(notificationProducer) //
				.inTopic(Topic.builder() //
						.name(applicationConfiguration.getKafka().getInTopic()) //
						.build()) //
				.outTopic(Topic.builder() //
						.name(applicationConfiguration.getKafka().getOutTopic()) //
						.build()) //
				.failTopic(Topic.builder() //
						.name(applicationConfiguration.getKafka().getFailTopic()) //
						.build()) //
				.build();
	}

}
