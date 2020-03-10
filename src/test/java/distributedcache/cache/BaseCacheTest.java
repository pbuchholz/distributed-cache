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
				.buildDefaultConfiguredBaseCacheWithRegions("ServerConfiguration1", "ServerConfiguration2");
		cache.put("ServerConfiguration1", new ConfigurationKey("TransactionTimeout"), ConfigurationValue.builder() //
				.name("TransactionTimeout") //
				.value("10") //
				.build());
		cache.put("ServerConfiguration1", new ConfigurationKey("MaxDatabaseConnections"), ConfigurationValue.builder() //
				.name("MaxDatabaseConnections") //
				.value("99") //
				.build());
		cache.put("ServerConfiguration2", new ConfigurationKey("TransactionTimeout"), ConfigurationValue.builder() //
				.name("TransactionTimeout") //
				.value("78") //
				.build());

		assertEquals("Wrong number of CacheRegions in cache.", 2, cache.getCacheRegions().size());

		assertNotNull("CacheRegion <ServerConfiguration1> not available.",
				cache.cacheRegionByName("ServerConfiguration1"));
		assertEquals("Wrong number of CacheEntries in CacheRegion <ServerConfiguration1>.", 2,
				cache.cacheRegionByName("ServerConfiguration1").cacheEntries().size());

		assertNotNull("CacheRegion <ServerConfiguration2> not available.",
				cache.cacheRegionByName("ServerConfiguration2"));
		assertEquals("Wrong number of CacheEntries in CacheRegion <ServerConfiguration2>.", 1,
				cache.cacheRegionByName("ServerConfiguration2").cacheEntries().size());

		cache.flushRegion("ServerConfiguration1");
		assertEquals("Wrong number of CacheEntries in CacheRegion <ServerConfiguration1> after flush.", 0,
				cache.cacheRegionByName("ServerConfiguration1").cacheEntries().size());
	}

}
