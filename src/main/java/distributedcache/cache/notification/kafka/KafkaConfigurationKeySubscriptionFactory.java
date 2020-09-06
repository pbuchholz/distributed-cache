package distributedcache.cache.notification.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import distributedcache.ApplicationConfiguration;
import distributedcache.cache.configuration.ConfigurationKey;
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
public class KafkaConfigurationKeySubscriptionFactory {

	@Autowired
	@KeySerializer
	@ValueSerializer("distributedcache.cache.configuration.boundary.NotificationSerializer")
	private Producer<Long, Notification<ConfigurationKey>> notificationProducer;
	
	@Autowired
	@Consume
	@ConsumerGroup("cache.notifications")
	@KeyDeserializer
	@ValueDeserializer("distributedcache.cache.configuration.boundary.NotificationDeserializer")
	private Consumer<Long, Notification<ConfigurationKey>> notificationConsumer;

	@Autowired
	private ApplicationConfiguration applicationConfiguration;

	@Bean
	public KafkaSubscription<Long, Notification<ConfigurationKey>> createKafkaSubscription() {
		return KafkaSubscription.<Long, Notification<ConfigurationKey>>builder() //
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
