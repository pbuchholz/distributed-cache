package distributedcache;

import java.io.Serializable;
import java.util.Set;

import distributedcache.notification.NotificationListener;

/**
 * Defines a cache which is segmented into {@link CacheRegion}s.
 * 
 * @author Philipp Buchholz
 */
public interface Cache<K extends CacheKey<K>, T extends Serializable> {

	/**
	 * Puts a value into the named region of the cache. The value is identified by
	 * its key.
	 * 
	 * @param regionName
	 * @param cacheEntry
	 */
	void put(String regionName, CacheEntry<K, T> cacheEntry);

	/**
	 * Gets a value from a named region by its key out of the cache.
	 * 
	 * @param regionName
	 * @param key
	 */
	T get(String regionName, K key);

	/**
	 * Flushes a named region of the cache.
	 */
	void flushRegion(String regionName);

	/**
	 * Returns a unmodifieable {@link Set} of the {@link CacheRegion}s available.
	 * 
	 * @return
	 */
	Set<CacheRegion<K, T>> getCacheRegions();

	/**
	 * Registers the {@link NotificationListener} with the {@link Cache}.
	 * 
	 * @param notificationListener
	 */
	void registerNotificationListener(NotificationListener notificationListener);

}
