package distributedcache.cache;

import static distributedcache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import distributedcache.configuration.ConfigurationKey;
import distributedcache.configuration.ConfigurationValue;

/**
 * Tests for {@link BaseCache}.
 * 
 * @author Philipp Buchholz
 */
public class BaseCacheTest {

	/**
	 * First puts a {@link ConfigurationValue} into the cache and then gets it
	 * again.
	 */
	@Test
	public void testPut() {
		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCacheTestBuilder
				.buildDefaultConfiguredBaseCacheWithRootRegion();
		cache.put(ROOT_CONFIGURATION_REGION, new ConfigurationKey("TransactionTimeout"), ConfigurationValue.builder() //
				.name("TransactionTimeout") //
				.value("10") //
				.build());

		ConfigurationValue configurationValue = cache.get(ROOT_CONFIGURATION_REGION,
				new ConfigurationKey("TransactionTimeout"));

		assertNotNull("ConfigurationValue putted could not got from cache.", configurationValue);
	}

}
