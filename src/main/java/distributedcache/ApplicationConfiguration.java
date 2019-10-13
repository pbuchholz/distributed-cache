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

	private String emitTopic;

	private String updateTopics;

	private String bootstrapServers;

	private String consumerGroup;

	private int cacheInvalidationPeriod;

	public String getEmitTopic() {
		return this.emitTopic;
	}

	@Inject
	@Value
	@Property("kafka.cache.emit.topic")
	public void setEmitTopic(String emitTopic) {
		this.emitTopic = emitTopic;
	}

	public List<String> getUpdateTopics() {
		return Arrays.asList(this.updateTopics.split(","));
	}

	@Inject
	@Value
	@Property("kafka.cache.notifications.updatetopics")
	public void setUpdateTopics(String updateTopics) {
		this.updateTopics = updateTopics;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	@Inject
	@Value
	@Property("kafka.bootstrap.servers")
	public void setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	@Inject
	@Value
	@Property("kafka.consumer.group")
	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public int getCacheInvalidationPeriod() {
		return cacheInvalidationPeriod;
	}

	@Inject
	@Value
	@Property("cache.invalidation.timer.period")
	public void setCacheInvalidationPeriod(int cacheInvalidationPeriod) {
		this.cacheInvalidationPeriod = cacheInvalidationPeriod;
	}

}
