package distributedcache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Represents the configuration of the Application.
 * 
 * @author Philipp Buchholz
 */
@ApplicationScoped
public class ApplicationConfiguration {

	@Inject
	@Value
	@Property("kafka.cache.notifications.topic")
	private String cacheNotificationsTopic;

	@Inject
	@Value
	@Property("kafka.cache.notifications.partition")
	private String cacheNotificationsPartition;

	@Inject
	@Value
	@Property("kafka.cache.notifications.peerpartition")
	private String cacheNotificationPeerPartition;

	@Inject
	@Value
	@Property("kafka.bootstrap.servers")
	private String bootstrapServers;

	@Inject
	@Value
	@Property("kafka.consumer.group")
	private String consumerGroup;

	public String getCacheNotificationsTopic() {
		return cacheNotificationsTopic;
	}

	public int getCacheNotificationsPartition() {
		return Integer.valueOf(cacheNotificationsPartition);
	}

	public int getPeerPartition() {
		return Integer.valueOf(cacheNotificationPeerPartition);
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

}
