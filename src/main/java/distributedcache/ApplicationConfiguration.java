package distributedcache;

import java.util.Optional;

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
	private Optional<String> in, out, fail;

	private Optional<String> bootstrapServers;

	private Optional<String> consumerGroup;

	private Optional<Integer> cacheInvalidationPeriod;

	public String getIn() {
		return this.in.get();
	}

	@Inject
	public void setIn(@Value @Property("kafka.cache.in.topic") Optional<String> in) {
		this.in = in;
	}

	public String getOut() {
		return this.out.get();
	}

	@Inject
	public void setOut(@Value @Property("kafka.cache.out.topic") Optional<String> out) {
		this.out = out;
	}

	public String getFail() {
		return this.fail.get();
	}

	@Inject
	public void setFail(@Value @Property("kafka.cache.fail.topic") Optional<String> fail) {
		this.fail = fail;
	}

	public String getBootstrapServers() {
		return bootstrapServers.get();
	}

	@Inject
	public void setBootstrapServers(@Value @Property("kafka.bootstrap.servers") Optional<String> bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}

	public String getConsumerGroup() {
		return consumerGroup.get();
	}

	@Inject
	public void setConsumerGroup(@Value @Property("kafka.consumer.group") Optional<String> consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public int getCacheInvalidationPeriod() {
		return cacheInvalidationPeriod.get();
	}

	@Inject
	public void setCacheInvalidationPeriod(@Value @Property(value = "cache.invalidation.timer.period", //
			type = Integer.class) Optional<Integer> cacheInvalidationPeriod) {
		this.cacheInvalidationPeriod = cacheInvalidationPeriod;
	}

}
