package distributedcache.cache.notification.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Factory which creates {@link Producer} instances.
 * 
 * @author Philipp Buchholz
 */
@Component
@Scope("application")
public class KafkaProducerFactory {

	@Autowired
	private KafkaConfigurationAdapter configurationAdapter;

	@Bean
	public <K, V> Producer<K, V> createProducer(InjectionPoint injectionPoint) {
		/* Preconditions. */
		assert null != injectionPoint;

		return new KafkaProducer<K, V>(configurationAdapter.createKafkaProducerProperties(injectionPoint));
	}

}
