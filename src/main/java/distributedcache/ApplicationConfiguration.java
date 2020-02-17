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

	/* Topic names for in, put and fail. */
	private String in, out, fail;

	private String bootstrapServers;

	private String consumerGroup;

	private int cacheInvalidationPeriod;

	public String getIn() {
		return this.in;
	}

	@Inject
	@Value
	@Property("kafka.cache.in.topic")
	public void setIn(String in) {
		this.in = in;
	}

	public String getOut() {
		return this.out;
	}

	@Inject
	@Value
	@Property("kafka.cache.out.topic")
	public void setOut(String out) {
		this.out = out;
	}

	public String getFail() {
		return this.fail;
	}

	@Inject
	@Value
	@Property("kafka.cache.fail.topic")
	public void setFail(String fail) {
		this.fail = fail;
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
