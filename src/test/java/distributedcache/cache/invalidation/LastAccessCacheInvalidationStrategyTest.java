package distributedcache.cache.invalidation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.Test;

import distributedcache.cache.CacheEntry;
import distributedcache.cache.DefaultCacheEntry;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;

/**
 * Defines tests for {@link LastAccessCacheInvalidationStrategy}.
 * 
 * @author Philipp Buchholz
 */
public class LastAccessCacheInvalidationStrategyTest {

	private InvalidationStrategy<ConfigurationKey, ConfigurationValue> invalidationStrategy //
			= new LastAccessCacheInvalidationStrategy<>();

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
	 * Builds an {@link CacheEntry} using buildConfigurationValue().
	 * 
	 * @return
	 */
	private CacheEntry<ConfigurationKey, ConfigurationValue> buildCacheEntry() {
		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = new DefaultCacheEntry<>();
		cacheEntry.setValue(this.buildConfigurationValue());
		cacheEntry.setKey(new ConfigurationKey("active.consumer.count"));
		cacheEntry.setCreated(System.currentTimeMillis());
		cacheEntry.setLastAccess(System.currentTimeMillis());
		cacheEntry.setValidationTimespan(Duration.ofSeconds(7).toMillis());
		return cacheEntry;
	}

	@Test
	public void shouldNotInvalidateAfter5Seconds() throws InterruptedException {

		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = this.buildCacheEntry();
		Thread.sleep(Duration.ofSeconds(5l).toMillis());
		boolean isInvalid = invalidationStrategy.isInvalid(cacheEntry);

		assertFalse("CacheEntry should be valid.", isInvalid);
	}

	@Test
	public void shouldInvalidateAfter10Seconds() throws InterruptedException {

		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = this.buildCacheEntry();
		Thread.sleep(Duration.ofSeconds(10l).toMillis());
		boolean isInvalid = invalidationStrategy.isInvalid(cacheEntry);

		assertTrue("CacheEntry should be invalid.", isInvalid);
	}

}
