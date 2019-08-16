package distributedcache.configuration;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import distributedcache.cache.BaseCache;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheConfiguration;

@ApplicationScoped
public class ConfigurationCacheProvider {

	public final static String ROOT_CONFIGURATION_REGION = "root.configuration";

	@Produces
	@ConfigurationCache
	public Cache<ConfigurationKey, ConfigurationValue> provideConfigurationCache() {
		return BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION) //
				.cacheConfiguration(createDefaultCacheConfiguration()) //
				.build();
	}

	private CacheConfiguration createDefaultCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofMinutes(10));
		return cacheConfiguration;
	}

}
