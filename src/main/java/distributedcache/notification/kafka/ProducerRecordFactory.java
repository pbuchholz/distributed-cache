package distributedcache.notification.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.ProducerRecord;

import distributedcache.ApplicationConfiguration;
import distributedcache.notification.Notification;

@ApplicationScoped
public class ProducerRecordFactory {

	@Inject
	private ApplicationConfiguration configuration;

	public enum PartitionKeys {
		/** Distribute to peer partition. */
		Peer
	}

	public ProducerRecord<PartitionKeys, Notification> createProducerRecord(PartitionKeys partitionKey,
			Notification notification) {
		return new ProducerRecord<PartitionKeys, Notification>(configuration.getCacheNotificationsTopic(), partitionKey,
				notification);
	}

}
