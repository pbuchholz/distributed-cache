package de.bu.eda.kafka.config;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Produces the properties as {@link Map} needed to establish a connection to
 * Kafka.
 * 
 * @author Philipp Buchholz
 */
@ApplicationScoped
public class KafkaPropertyBuilder {

	private static final String KEY_SERIALIZER = "key.serializer";
	private static final String VALUE_SERIALIZER = "value.serializer";
	private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";

	@Inject
	@Config(key = KEY_SERIALIZER)
	private String keySerializer;

	@Inject
	@Config(key = VALUE_SERIALIZER)
	private String valueSerializer;

	@Inject
	@Config(key = BOOTSTRAP_SERVERS)
	private String bootstrapServers;

	@Produces
	@KafkaProperties
	public Map<String, Object> buildKafkaProperties() {
		Map<String, Object> kafkaProperties = new HashMap<>();
		kafkaProperties.put(KEY_SERIALIZER, this.keySerializer);
		kafkaProperties.put(VALUE_SERIALIZER, this.valueSerializer);
		kafkaProperties.put(BOOTSTRAP_SERVERS, this.bootstrapServers);
		return kafkaProperties;
	}

}
