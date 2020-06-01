package distributedcache.cache.notification.kafka;

import static distributedcache.Reflections.valueFromAnnotatedElement;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class KafkaConfigurationAdapter {

	@Autowired
	private ApplicationConfiguration applicationConfiguration;

	/**
	 * Creates the {@link Properties} needed to setup a KafkaConsumer.
	 * 
	 * @param injectionPoint
	 * @return
	 */
	public Properties createKafkaConsumerProperties(InjectionPoint injectionPoint) {

		AnnotatedElement annotatedElement = injectionPoint.getAnnotatedElement();

		assert annotatedElement.isAnnotationPresent(KeyDeserializer.class);
		assert annotatedElement.isAnnotationPresent(ValueDeserializer.class);

		try {
			return this.createBasePropertiesBuilder(injectionPoint) //
					.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
							valueFromAnnotatedElement(annotatedElement, KeyDeserializer.class,
									annotatedElement.getAnnotation(KeyDeserializer.class))) //
					.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
							valueFromAnnotatedElement(annotatedElement, ValueDeserializer.class,
									annotatedElement.getAnnotation(ValueDeserializer.class))) //
					.put(ConsumerConfig.GROUP_ID_CONFIG, applicationConfiguration.getKafka().getConsumerGroup()) //
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

		AnnotatedElement annotatedElement = injectionPoint.getAnnotatedElement();

		assert annotatedElement.isAnnotationPresent(KeySerializer.class);
		assert annotatedElement.isAnnotationPresent(ValueSerializer.class);

		try {
			return this.createBasePropertiesBuilder(injectionPoint) //
					.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
							valueFromAnnotatedElement(annotatedElement, KeySerializer.class,
									annotatedElement.getAnnotation(KeySerializer.class))) //
					.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
							valueFromAnnotatedElement(annotatedElement, ValueSerializer.class,
									annotatedElement.getAnnotation(ValueSerializer.class))) //
					.build();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private PropertiesBuilder createBasePropertiesBuilder(InjectionPoint injectionPoint) {
		assert null != injectionPoint;
		return PropertiesBuilder.builder() //
				.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
						applicationConfiguration.getKafka().getBootStrapServers());
	}

}
