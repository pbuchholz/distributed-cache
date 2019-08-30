package distributedcache;

import java.util.Arrays;
import java.util.List;

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
	@Property("kafka.cache.emit.topic")
	private String emitTopic;

	@Inject
	@Value
	@Property("kafka.cache.notifications.updatetopics")
	private String updateTopics;

	@Inject
	@Value
	@Property("kafka.bootstrap.servers")
	private String bootstrapServers;

	@Inject
	@Value
	@Property("kafka.consumer.group")
	private String consumerGroup;

	@Inject
	@Value
	@Property("cache.invalidation.timer.period")
	private String cacheInvalidationPeriod;

	public String getEmitTopic() {
		return this.emitTopic;
	}

	public List<String> getUpdateTopics() {
		return Arrays.asList(this.updateTopics.split(","));
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public int getCacheInvalidationPeriod() {
		return Integer.valueOf(cacheInvalidationPeriod);
	}

}
