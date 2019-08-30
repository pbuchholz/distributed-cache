package distributedcache.configuration;

import java.time.Duration;

import javax.annotation.Resource;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import distributedcache.ApplicationConfiguration;
import distributedcache.cache.BaseCache;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheConfiguration;

@ApplicationScoped
public class ConfigurationCacheProvider {

	public final static String ROOT_CONFIGURATION_REGION = "root.configuration";

	@Resource
	private TimerService timerService;

	@Inject
	private ApplicationConfiguration configuration;

	@Produces
	@ConfigurationCache
	public Cache<ConfigurationKey, ConfigurationValue> provideConfigurationCache() {

		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
				.cacheRegion(ROOT_CONFIGURATION_REGION) //
				.cacheConfiguration(createDefaultCacheConfiguration()) //
				.build();

		int cacheInvalidationPeriod = configuration.getCacheInvalidationPeriod();
		Timer timer = timerService.createIntervalTimer(cacheInvalidationPeriod, cacheInvalidationPeriod, null);
		
		return cache;
	}
	
	@Timeout
	public void invalidate(Timer timer) {
		
	}

	
	private CacheConfiguration createDefaultCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.registerValidationTimespan(ConfigurationValue.class, Duration.ofMinutes(10));
		return cacheConfiguration;
	}

}
