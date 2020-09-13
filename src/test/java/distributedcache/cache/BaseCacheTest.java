package distributedcache.cache;

import static distributedcache.cache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;

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

	/**
	 * First fills a region with {@link ConfigurationValue}s and then flushes that
	 * region and ensures that the region is empty afterwards.
	 */
	@Test
	public void testFlushRegion() {
		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCacheTestBuilder
				.buildDefaultConfiguredBaseCacheWithRootRegion();

		cache.put(ROOT_CONFIGURATION_REGION, new ConfigurationKey("TransactionTimeout"), ConfigurationValue.builder() //
				.name("TransactionTimeout") //
				.value("10") //
				.build());
		cache.put("sub", new ConfigurationKey("MaxDatabaseConnections"), ConfigurationValue.builder() //
				.name("MaxDatabaseConnections") //
				.value("99") //
				.build());

		assertEquals("Wrong number of cache entries in root cache region.", 1,
				cache.cacheRegionByName(ROOT_CONFIGURATION_REGION).getAll().size());
		assertEquals("TransactionTimeout has wrong value.", "10", cache.cacheRegionByName(ROOT_CONFIGURATION_REGION)
				.get(new ConfigurationKey("TransactionTimeout")).getValue());

		assertEquals("Wrong number of cache entries in root cache region.", 1,
				cache.cacheRegionByName("sub").getAll().size());
		assertEquals("MaxDatabaseConnections has wrong value.", "99",
				cache.cacheRegionByName("sub").get(new ConfigurationKey("MaxDatabaseConnections")).getValue());

		cache.flushRegion("sub");
		assertEquals("Cache region [sub] has not been flushed.", 0, cache.cacheRegionByName("sub").getAll().size());
	}

	/**
	 * Tests if {@link Cache#cacheRegionByName(String)} works correctly.
	 */
	@Test
	public void testfindRegionByName() {
		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCacheTestBuilder
				.buildDefaultConfiguredWithHierarchicalRegion();

		Cache<ConfigurationKey, ConfigurationValue> found = cache.cacheRegionByName("server-1-configuration");
		assertNotNull("Region could not be found.", found);
	}

}
