package distributedcache.notification.kafka;

import java.util.Map;

import javax.enterprise.inject.spi.CDI;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import distributedcache.ApplicationConfiguration;
import distributedcache.notification.Notification;
import distributedcache.notification.kafka.ProducerRecordFactory.PartitionKeys;

/**
 * Distributes the Notifications according to the {@link PartitionKeys}.
 * 
 * @author Philipp Buchholz
 */
public class PeerPartitioner implements Partitioner {

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		int partition = 0;

		Notification notification = (Notification)value;
		
		if (key.getClass().isInstance(PartitionKeys.class)) {
			PartitionKeys partitionKey = (PartitionKeys) key;

			ApplicationConfiguration applicationConfiguration = CDI.current().select(ApplicationConfiguration.class)
					.get();
			switch (partitionKey) {
			case Peer:
				partition = applicationConfiguration.getPeerPartition();
				break;
			default:
				break;
			}
		}
		
		if(partition == notification.getSource())
			return -1;

		return partition;
	}

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public void close() {
	}

}
