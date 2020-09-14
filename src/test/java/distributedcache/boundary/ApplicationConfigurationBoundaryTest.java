package distributedcache.boundary;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import distributedcache.ApplicationConfiguration;
import distributedcache.ApplicationConfiguration.Kafka;

@WebMvcTest(ApplicationConfigurationBoundary.class)
public class ApplicationConfigurationBoundaryTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ApplicationConfiguration configuration;

	@Before
	public void mockConfiguration() {
		Kafka kafka = new Kafka();
		kafka.setBootStrapServers(Arrays.asList("localhost:9091"));
		kafka.setFailTopic("fail-instance-1");
		kafka.setInTopic("in-instance-1");
		kafka.setOutTopic("out-instance-2");
		kafka.setConsumerGroup("cache-updates");
		when(configuration.getInvalidationTimerPeriod()).thenReturn(10);
		when(configuration.getKafka()).thenReturn(kafka);
	}

	@Test
	public void greetingShouldReturnMessageFromService() throws Exception {
		this.mockMvc.perform(get("/application-configuration")).andDo(print()).andExpect(status().isOk());
	}
}