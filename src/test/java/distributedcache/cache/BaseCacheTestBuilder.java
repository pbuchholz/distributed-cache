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

	public static <K extends CacheKey<K>, V extends Serializable> Cache<K, V> buildDefaultConfiguredBaseCacheWithRootRegion(
			CacheRegion<K, V>... cacheRegionsBelowRoot) {

		BaseCache.BaseCacheBuilder<K, V> builder = BaseCache.<K, V>builder() //
				.cacheRegion(CacheRegion.<K, V>cacheRegionBuilder() //
						.regionName(ROOT_CONFIGURATION_REGION) //
						.invalidationStrategy(new InvalidationStrategy.None<>()) //
						.cacheConfiguration(buildDefaultCacheConfiguration()) //
						.cacheRegion(CacheRegion.<K, V>cacheRegionBuilder() //
								.regionName("sub") //
								.invalidationStrategy(new InvalidationStrategy.None<>()) //
								.cacheConfiguration(buildDefaultCacheConfiguration()) //
								.build()) //
						.cacheRegions(cacheRegionsBelowRoot) //
						.build()) //
				.invalidationStrategy(new InvalidationStrategy.None<>()) //
				.cacheConfiguration(buildDefaultCacheConfiguration());

		return builder.build();
	}

	public static <K extends CacheKey<K>, V extends Serializable> Cache<K, V> buildHierarchicalCacheRegion() {
		return BaseCache.<K, V>builder() //
				.cacheConfiguration(BaseCacheTestBuilder.buildDefaultCacheConfiguration()) //
				.cacheRegion(CacheRegion.<K, V>cacheRegionBuilder() //
						.regionName("server-1-configuration") //
						.invalidationStrategy(new InvalidationStrategy.None<>()) //
						.cacheConfiguration(buildDefaultCacheConfiguration()) //
						.cacheRegion(CacheRegion.<K, V>cacheRegionBuilder() //
								.regionName("java-configuration") //
								.invalidationStrategy(new InvalidationStrategy.None<>()) //
								.cacheConfiguration(buildDefaultCacheConfiguration()) //
								.build()) //
						.build()) //
				.build();
	}

	public static <K extends CacheKey<K>, V extends Serializable> Cache<K, V> buildDefaultConfiguredWithHierarchicalRegion() {
		return buildDefaultConfiguredBaseCacheWithRootRegion(CacheRegion.<K, V>cacheRegionBuilder() // )
				.regionName("another-sub-region-1") //
				.cacheConfiguration(buildDefaultCacheConfiguration()) //
				.invalidationStrategy(new InvalidationStrategy.None<>()) //
				.build(),
				CacheRegion.<K, V>cacheRegionBuilder() // )
						.regionName("another-sub-region-2") //
						.cacheConfiguration(buildDefaultCacheConfiguration()) //
						.invalidationStrategy(new InvalidationStrategy.None<>()) //
						.build(),
				CacheRegion.<K, V>cacheRegionBuilder() // )
						.regionName("another-sub-region-3") //
						.cacheConfiguration(buildDefaultCacheConfiguration()) //
						.invalidationStrategy(new InvalidationStrategy.None<>()) //
						.build());
	}

}
