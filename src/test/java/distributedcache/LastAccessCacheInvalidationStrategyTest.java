//package distributedcache;
//
//import static distributedcache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.mockito.Mockito.when;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import distributedcache.cache.Cache;
//import distributedcache.cache.CacheInvalidationStrategy;
//import distributedcache.cache.LastAccessCacheInvalidationStrategy;
//import distributedcache.configuration.ConfigurationCacheProvider;
//import distributedcache.configuration.ConfigurationKey;
//import distributedcache.configuration.ConfigurationValue;
//
///**
// * Defines tests for {@link LastAccessCacheInvalidationStrategy}.
// * 
// * @author Philipp Buchholz
// */
//public class LastAccessCacheInvalidationStrategyTest {
//
//	private Cache<ConfigurationKey, ConfigurationValue> cache;
//	private CacheInvalidationStrategy<ConfigurationKey, ConfigurationValue> invalidationStrategy //
//			= new LastAccessCacheInvalidationStrategy<>();
//
//	@Before
//	public void prepareCache() {
//		ConfigurationCacheProvider configurationCacheProvider = new ConfigurationCacheProvider();
//		this.cache = configurationCacheProvider.provideConfigurationCache();
//	}
//
//	@Test
//	public void shouldNotInvalidateAccessed() {
//		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");
//
//		cache.put(ROOT_CONFIGURATION_REGION, configurationKey, ConfigurationValue.builder() //
//				.name("active.consumer.count") //
//				.value("10") //
//				.build());
//
//		/* Access CacheEntry. */
//		cache.get(ROOT_CONFIGURATION_REGION, configurationKey);
//		invalidationStrategy.invalidate(cache);
//
//		assertNotNull("LastAccessCacheInvalidationStrategy invalidated valid CacheEntry.",
//				cache.get(ROOT_CONFIGURATION_REGION, configurationKey));
//
//	}
//	
//	public void shouldInvalidateOld() {
//		ConfigurationKey configurationKey = new ConfigurationKey("active.consumer.count");
//
//		cache.put(ROOT_CONFIGURATION_REGION, configurationKey, ConfigurationValue.builder() //
//				.name("active.consumer.count") //
//				.value("10") //
//				.build());
//
//		/* Access CacheEntry. */
//		ConfigurationValue configurationValue = cache.get(ROOT_CONFIGURATION_REGION, configurationKey);
//		invalidationStrategy.invalidate(cache);
//
//		
//		
//		
//		assertNull("ConfigurationValue has not been invalidated.", configurationValue);
//	}
//
//}
