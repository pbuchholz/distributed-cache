package distributedcache.cache.notification.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import distributedcache.cache.notification.Subscription;

/**
 * Represents the {@link Subscription} to a Kafka broker.
 * 
 * @author Philipp Buchholz
 *
 * @param <K> The type of key.
 * @param <V> The type of value.
 */
public class KafkaSubscription<K, V> implements Subscription {

	private Consumer<K, V> consumer;
	private Producer<K, V> producer;

	/* Incoming messages like Notifications. */
	private Topic inTopic;

	/* Outgoing messages like acks. */
	private Topic outTopic;

	/* Outgoing messages for handling failures. */
	private Topic failTopic;

	private KafkaSubscription() {
		/* Only Builder. */
	}

	public static <K, V> Builder<K, V> builder() {
		return new Builder<K, V>();
	}

	public boolean isConfiguredForPublishing() {
		return this.outTopic.isPresent();
	}

	public static class Builder<K, V> {
		private KafkaSubscription<K, V> kafkaSubscription;

		public Builder() {
			this.kafkaSubscription = new KafkaSubscription<>();
		}

		public Builder<K, V> consumer(Consumer<K, V> consumer) {
			this.kafkaSubscription.consumer = consumer;
			return this;
		}

		public Builder<K, V> producer(Producer<K, V> producer) {
			this.kafkaSubscription.producer = producer;
			return this;
		}

		public Builder<K, V> inTopic(Topic inTopic) {
			this.kafkaSubscription.inTopic = inTopic;
			return this;
		}

		public Builder<K, V> outTopic(Topic outTopic) {
			this.kafkaSubscription.outTopic = outTopic;
			return this;
		}

		public Builder<K, V> failTopic(Topic failTopic) {
			this.kafkaSubscription.failTopic = failTopic;
			return this;
		}

		public KafkaSubscription<K, V> build() {
			return this.kafkaSubscription;
		}

	}

	@Override
	public Topic inTopic() {
		return inTopic;
	}

	@Override
	public Topic outTopic() {
		return outTopic;
	}

	@Override
	public Topic failTopic() {
		return failTopic;
	}

	public Consumer<K, V> getConsumer() {
		return consumer;
	}

	public Producer<K, V> getProducer() {
		return producer;
	}

}
