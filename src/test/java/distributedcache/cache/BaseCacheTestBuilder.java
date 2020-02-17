package distributedcache.cache;

import static distributedcache.cache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;

import java.io.Serializable;
import java.time.Duration;

import distributedcache.cache.configuration.ConfigurationValue;

/**
 * 
 * @author philippbuchholz
 *
 */
public class BaseCacheTestBuilder {

	private BaseCacheTestBuilder() {

	}

	public static CacheConfiguration buildDefaultCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofSeconds(10));
		return cacheConfiguration;
	}

	public static <K extends CacheKey<K>, V extends Serializable> BaseCache<K, V> buildDefaultConfiguredBaseCacheWithRootRegion() {
		return BaseCache.<K, V>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION) //
				.cacheConfiguration(buildDefaultCacheConfiguration()) //
				.build();
	}

}
