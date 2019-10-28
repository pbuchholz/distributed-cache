package distributedcache;

import static distributedcache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

import distributedcache.cache.BaseCache;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheConfiguration;
import distributedcache.cache.CacheInvalidationStrategy;
import distributedcache.cache.LastAccessCacheInvalidationStrategy;
import distributedcache.configuration.ConfigurationKey;
import distributedcache.configuration.ConfigurationValue;

/**
 * Defines tests for {@link LastAccessCacheInvalidationStrategy}.
 * 
 * @author Philipp Buchholz
 */
public class LastAccessCacheInvalidationStrategyTest {

	private Cache<ConfigurationKey, ConfigurationValue> cache;
	private CacheInvalidationStrategy<ConfigurationKey, ConfigurationValue> invalidationStrategy //
			= new LastAccessCacheInvalidationStrategy<>();

	@Before
	public void prepareCache() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofSeconds(10));

		this.cache = BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION) //
				.cacheConfiguration(cacheConfiguration) //
				.build();
	}

	/**
	 * Builds a {@link ConfigurationValue} for testing purposes.
	 * 
	 * @return
	 */
	private ConfigurationValue buildConfigurationValue() {
		return ConfigurationValue.builder() //
				.name("active.consumer.count") //
				.value("10") //
				.build();
	}

	/**
	 * Puts a new {@link ConfigurationValue} into the cache and then waits for 5
	 * seconds. The putted {@link ConfigurationValue} should still be valid after
	 * that time span.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void shouldNotInvalidateAfter5Seconds() throws InterruptedException {
		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");

		cache.put(ROOT_CONFIGURATION_REGION, configurationKey, this.buildConfigurationValue());

		/* Access CacheEntry. */
		cache.get(ROOT_CONFIGURATION_REGION, configurationKey);

		/* After five seconds the cache entry is still valid. */
		Thread.sleep(Duration.ofSeconds(5l).toMillis());

		/* invalidate CacheEntries. */
		invalidationStrategy.invalidate(cache);

		assertNotNull("LastAccessCacheInvalidationStrategy invalidated valid CacheEntry.",
				cache.get(ROOT_CONFIGURATION_REGION, configurationKey));

	}

	/**
	 * Puts a new {@link ConfigurationValue} into the cache and then waits for 15
	 * seconds. The putted {@link ConfigurationValue} should have been invalidated
	 * in the meantime.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void shouldInvalidateAfter10Seconds() throws InterruptedException {
		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");

		cache.put(ROOT_CONFIGURATION_REGION, configurationKey, this.buildConfigurationValue());

		/* After 15 seconds the cache entry is not valid any more. */
		Thread.sleep(Duration.ofSeconds(15l).toMillis());

		/* Invaldate cache entries. */
		invalidationStrategy.invalidate(cache);

		assertNull("ConfigurationValue has not been invalidated.",
				cache.get(ROOT_CONFIGURATION_REGION, configurationKey));
	}

}
