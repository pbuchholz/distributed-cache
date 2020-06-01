package distributedcache;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Represents the configuration of the Application.
 * 
 * @author Philipp Buchholz
 */
@Configuration
@ConfigurationProperties("cache")
public class ApplicationConfiguration {

	public final static class Kafka {

		private String inTopic;

		private String outTopic;

		private String failTopic;

		private String consumerGroup;

		private List<String> bootStrapServers;

		public String getInTopic() {
			return inTopic;
		}

		public void setInTopic(String inTopic) {
			this.inTopic = inTopic;
		}

		public String getOutTopic() {
			return outTopic;
		}

		public void setOutTopic(String outTopic) {
			this.outTopic = outTopic;
		}

		public String getFailTopic() {
			return failTopic;
		}

		public void setFailTopic(String failTopic) {
			this.failTopic = failTopic;
		}

		public String getConsumerGroup() {
			return consumerGroup;
		}

		public void setConsumerGroup(String consumerGroup) {
			this.consumerGroup = consumerGroup;
		}

		public List<String> getBootStrapServers() {
			return bootStrapServers;
		}

		public void setBootStrapServers(List<String> bootStrapServers) {
			this.bootStrapServers = bootStrapServers;
		}

	}

	private Kafka kafka = new Kafka();

	private int invalidationTimerPeriod;

	public Kafka getKafka() {
		return this.kafka;
	}

	public void setKafka(Kafka kafka) {
		this.kafka = kafka;
	}

	public int getInvalidationTimerPeriod() {
		return invalidationTimerPeriod;
	}

	public void setInvalidationTimerPeriod(int invalidationTimerPeriod) {
		this.invalidationTimerPeriod = invalidationTimerPeriod;
	}

	@Bean
	public SchedulingTaskExecutor createSchedulingTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("distributed-cache");
		return threadPoolTaskExecutor;
	}

}
