package distributedcache.cache.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import distributedcache.cache.BaseCache;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheConfiguration;

@Component
@Scope("application")
public class ConfigurationCacheProvider {

	public final static String ROOT_CONFIGURATION_REGION = "root.configuration";

	@Bean
	@ConfigurationCache
	public Cache<ConfigurationKey, ConfigurationValue> provideConfigurationCache() {

		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION) //
				.cacheConfiguration(createDefaultCacheConfiguration()) //
				.build();

		return cache;
	}

	private CacheConfiguration createDefaultCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofMinutes(10));
		return cacheConfiguration;
	}

}
