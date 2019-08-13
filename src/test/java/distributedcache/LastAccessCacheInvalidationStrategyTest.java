package distributedcache;

import static distributedcache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

import distributedcache.configuration.ConfigurationCacheProvider;
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
		ConfigurationCacheProvider configurationCacheProvider = new ConfigurationCacheProvider();
		this.cache = configurationCacheProvider.provideConfigurationCache();
	}

	@Test
	public void shouldNotInvalidateNotAccessed() {
		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");
		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = this
				.createConfigurationValueCacheEntry(configurationKey, Duration.ofMinutes(10));

		cache.put(ROOT_CONFIGURATION_REGION, cacheEntry);
		invalidationStrategy.invalidate(cache);

		assertNotNull("LastAccessCacheInvalidationStrategy invalidated valid CacheEntry.",
				cache.get(ROOT_CONFIGURATION_REGION, configurationKey));

	}

	@Test(expected = NullPointerException.class)
	public void shouldInvalidateNotAccessed() {
		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");
		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = this
				.createConfigurationValueCacheEntry(configurationKey, Duration.ofMillis(0));

		cache.put(ROOT_CONFIGURATION_REGION, cacheEntry);
		invalidationStrategy.invalidate(cache);

		cache.get(ROOT_CONFIGURATION_REGION, configurationKey);
	}

	private CacheEntry<ConfigurationKey, ConfigurationValue> createConfigurationValueCacheEntry(
			ConfigurationKey configurationKey, Duration duration) {
		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = CacheEntry
				.<ConfigurationKey, ConfigurationValue>builder() //
				.created(System.currentTimeMillis()) //
				.key(configurationKey) //
				.value(ConfigurationValue.builder() //
						.name("active.consumer.count") //
						.value("10") //
						.build()) //
				.validationTimespan(duration.toMillis()) //
				.build();
		return cacheEntry;
	}

}
