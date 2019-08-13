package distributedcache.notification.kafka;

import static distributedcache.Reflections.fromAnnotated;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

import distributedcache.ApplicationConfiguration;
import distributedcache.PropertiesBuilder;
import distributedcache.configuration.boundary.Consume;
import distributedcache.configuration.boundary.KeyDeserializer;
import distributedcache.configuration.boundary.KeySerializer;
import distributedcache.configuration.boundary.ValueDeserializer;
import distributedcache.configuration.boundary.ValueSerializer;

/**
 * Factory which creates Kafka objects like consumers and producers;
 * 
 * @author Philipp Buchholz
 */
@ApplicationScoped
public class KafkaFactory implements Serializable {

	private static final long serialVersionUID = -671821616766899185L;

	@Inject
	private ApplicationConfiguration configuration;

	@Consume
	@Produces
	public <K, V> Consumer<K, V> createConsumer(InjectionPoint injectionPoint) {
		/* Preconditions. */
		assert null != injectionPoint;

		Properties kafkaConsumerProperties = this.createKafkaConsumerProperties(injectionPoint);
		UUID clientId = UUID.randomUUID();
		kafkaConsumerProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId.toString());

		KafkaConsumer<K, V> kafkaConsumer = new KafkaConsumer<K, V>(kafkaConsumerProperties);

		List<TopicPartition> assignedPartitions = new ArrayList<>();
		assignedPartitions.add(new TopicPartition(this.configuration.getCacheNotificationsTopic(),
				this.configuration.getCacheNotificationsPartition()));
		kafkaConsumer.assign(assignedPartitions);

		return kafkaConsumer;
	}

	@Produces
	public <K, V> Producer<K, V> createProducer(InjectionPoint injectionPoint) {
		/* Preconditions. */
		assert null != injectionPoint;

		return new KafkaProducer<K, V>(this.createKafkaProducerProperties(injectionPoint));
	}

	private Properties createKafkaConsumerProperties(InjectionPoint injectionPoint) {
		assert null != injectionPoint;

		Annotated annotated = injectionPoint.getAnnotated();

		try {
			return PropertiesBuilder.builder() //
					.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers()) //
					.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroup()) //
					.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
							fromAnnotated(annotated, KeyDeserializer.class,
									annotated.getAnnotation(KeyDeserializer.class))) //
					.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
							fromAnnotated(annotated, ValueDeserializer.class,
									annotated.getAnnotation(ValueDeserializer.class))) //
					.build();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private Properties createKafkaProducerProperties(InjectionPoint injectionPoint) {
		assert null != injectionPoint;

		Annotated annotated = injectionPoint.getAnnotated();

		try {
			return PropertiesBuilder.builder() //
					.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers()) //
					.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
							fromAnnotated(annotated, KeySerializer.class, annotated.getAnnotation(KeySerializer.class))) //
					.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
							fromAnnotated(annotated, ValueSerializer.class,
									annotated.getAnnotation(ValueSerializer.class))) //
					.build();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}

	}

}
