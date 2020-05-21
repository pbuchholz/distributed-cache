package distributedcache.cache.configuration.boundary;

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import distributedcache.cache.notification.Notification;

/**
 * Tests for {@link ConfigurationBoundary}.
 * 
 * @author Philipp Buchholz
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationBoundaryTest {

	@InjectMocks
	private ConfigurationBoundary configurationBoundary;

	@Mock
	private Consumer<Long, Notification> notificationConsumer;

	@Test
	public void testGetConfigurationCache() {


		
		
	}

}
