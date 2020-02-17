package distributedcache.cache.notification.kafka;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

/**
 * Factory which creates {@link Producer} instances.
 * 
 * @author Philipp Buchholz
 */
@Dependent
public class KafkaProducerFactory {

	@Inject
	private KafkaConfigurationAdapter configurationAdapter;

	@Produces
	public <K, V> Producer<K, V> createProducer(InjectionPoint injectionPoint) {
		/* Preconditions. */
		assert null != injectionPoint;

		return new KafkaProducer<K, V>(configurationAdapter.createKafkaProducerProperties(injectionPoint));
	}

}
