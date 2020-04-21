package distributedcache.cache.notification.kafka;

import static distributedcache.Reflections.valueFromAnnotated;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import distributedcache.ApplicationConfiguration;
import distributedcache.PropertiesBuilder;
import distributedcache.cache.configuration.boundary.KeyDeserializer;
import distributedcache.cache.configuration.boundary.KeySerializer;
import distributedcache.cache.configuration.boundary.ValueDeserializer;
import distributedcache.cache.configuration.boundary.ValueSerializer;

/**
 * Adapts an {@link ApplicationConfiguration} to read properties for the
 * connection to Apache Kafka.
 * 
 * @author Philipp Buchholz
 */
@Dependent
public class KafkaConfigurationAdapter {

	@Inject
	private ApplicationConfiguration applicationConfiguration;

	/**
	 * Creates the {@link Properties} needed to setup a KafkaConsumer.
	 * 
	 * @param injectionPoint
	 * @return
	 */
	public Properties createKafkaConsumerProperties(InjectionPoint injectionPoint) {

		Annotated annotated = injectionPoint.getAnnotated();

		assert annotated.isAnnotationPresent(KeyDeserializer.class);
		assert annotated.isAnnotationPresent(ValueDeserializer.class);

		try {
			return this.createBasePropertiesBuilder(injectionPoint) //
					.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
							valueFromAnnotated(annotated, KeyDeserializer.class,
									annotated.getAnnotation(KeyDeserializer.class))) //
					.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
							valueFromAnnotated(annotated, ValueDeserializer.class,
									annotated.getAnnotation(ValueDeserializer.class))) //
					.put(ConsumerConfig.GROUP_ID_CONFIG, applicationConfiguration.getConsumerGroup()) //
					.build();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates the {@link Properties} needed to setup a KafkaProducer.
	 * 
	 * @param injectionPoint
	 * @return
	 */
	public Properties createKafkaProducerProperties(InjectionPoint injectionPoint) {

		Annotated annotated = injectionPoint.getAnnotated();

		assert annotated.isAnnotationPresent(KeySerializer.class);
		assert annotated.isAnnotationPresent(ValueSerializer.class);

		try {
			return this.createBasePropertiesBuilder(injectionPoint) //
					.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
							valueFromAnnotated(annotated, KeySerializer.class,
									annotated.getAnnotation(KeySerializer.class))) //
					.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
							valueFromAnnotated(annotated, ValueSerializer.class,
									annotated.getAnnotation(ValueSerializer.class))) //
					.build();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private PropertiesBuilder createBasePropertiesBuilder(InjectionPoint injectionPoint) {
		assert null != injectionPoint;
		return PropertiesBuilder.builder() //
				.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfiguration.getBootstrapServers());
	}

}
