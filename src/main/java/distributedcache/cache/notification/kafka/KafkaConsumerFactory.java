package distributedcache.cache.notification.kafka;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import distributedcache.cache.configuration.boundary.Consume;

/**
 * Factory which creates Kafka objects like consumers and producers;
 * 
 * @author Philipp Buchholz
 */
@Dependent
public class KafkaConsumerFactory implements Serializable {

	private static final long serialVersionUID = -671821616766899185L;

	@Inject
	private KafkaConfigurationAdapter configurationAdapter;

	@Consume
	@Produces
	public <K, V> Consumer<K, V> createConsumer(InjectionPoint injectionPoint) {
		/* Preconditions. */
		assert null != injectionPoint;

		Properties kafkaConsumerProperties = this.configurationAdapter.createKafkaConsumerProperties(injectionPoint);
		UUID clientId = UUID.randomUUID();
		kafkaConsumerProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId.toString());

		return new KafkaConsumer<K, V>(kafkaConsumerProperties);
	}

}
