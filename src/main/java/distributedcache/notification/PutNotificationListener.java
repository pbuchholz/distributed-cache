//package distributedcache.notification;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.event.Observes;
//import javax.enterprise.inject.Default;
//import javax.inject.Inject;
//
//import distributedcache.Cache;
//import distributedcache.CacheEntry;
//import distributedcache.configuration.ConfigurationCache;
//import distributedcache.configuration.ConfigurationKey;
//import distributedcache.configuration.ConfigurationValue;
//
//@Default
//@ApplicationScoped
//public class PutNotificationListener implements NotificationListener {
//
//	@Inject
//	@ConfigurationCache
//	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;
//
//	@Override
//	public void onNotification(@Observes Notification notification) {
//		PutNotification putNotification = (PutNotification) notification;
//
//		configurationCache.put(putNotification.getRegionName(),
//				this.buildCacheEntryForPutNotification(putNotification));
//	}
//
//	private CacheEntry<ConfigurationKey, ConfigurationValue> buildCacheEntryForPutNotification(
//			PutNotification putNotification) {
//		return CacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
//				.key((ConfigurationKey) putNotification.getCacheKey()) //
//				.created(System.currentTimeMillis()) //
//				.value((ConfigurationValue) putNotification.getValue()) //
//				.build();
//	}
//
//}