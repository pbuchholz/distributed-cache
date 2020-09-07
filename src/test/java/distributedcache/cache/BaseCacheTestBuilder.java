package distributedcache.cache;

import static distributedcache.cache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;

import java.io.Serializable;
import java.time.Duration;

import distributedcache.cache.configuration.ConfigurationValue;
import distributedcache.cache.invalidation.InvalidationStrategy;

/**
 * Builds prepared instances of {@link BaseCache} for testing purposes.
 * 
 * @author Philipp Buchholz
 */
public class BaseCacheTestBuilder {

	private BaseCacheTestBuilder() {

	}

	public static CacheConfiguration buildDefaultCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofSeconds(10));
		return cacheConfiguration;
	}

	public static <K extends CacheKey<K>, V extends Serializable> Cache<K, V> buildDefaultConfiguredBaseCacheWithRootRegion() {
		return BaseCache.<K, V>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION, BaseCache.<K, V>builder() //
						.cacheRegion("sub", BaseCache.<K, V>builder() //
								.invalidationStrategy(new InvalidationStrategy.None<>()) //
								.cacheConfiguration(buildDefaultCacheConfiguration())//
								.build()) //
						.invalidationStrategy(new InvalidationStrategy.None<>()) //
						.cacheConfiguration(buildDefaultCacheConfiguration()) //
						.build()) //
				.invalidationStrategy(new InvalidationStrategy.None<>()) //
				.cacheConfiguration(buildDefaultCacheConfiguration()) //
				.build();
	}

}
