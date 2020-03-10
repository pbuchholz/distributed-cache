package distributedcache.cache;

import java.io.Serializable;
import java.util.Set;

/**
 * Defines a cache which is segmented into {@link DefaultCacheRegion}s.
 * 
 * @author Philipp Buchholz
 */
public interface Cache<K extends CacheKey<K>, T extends Serializable> {

	/**
	 * Puts a value into the named region of the cache. The value is identified by
	 * its key. Internally a new CacheEntry is build and put into the region with
	 * the passed in regionName.
	 * 
	 * @param regionName
	 * @param key        The key which identifies the value.
	 * @param value      The value.
	 */
	void put(String regionName, K key, T value);

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
	 * Returns the {@link DefaultCacheRegion} with the passed in name.
	 * 
	 * @param regionName
	 * @return
	 */
	CacheRegion<K, T> cacheRegionByName(String regionName);

	/**
	 * Returns a unmodifieable {@link Set} of the {@link DefaultCacheRegion}s
	 * available.
	 * 
	 * @return
	 */
	Set<CacheRegion<K, T>> getCacheRegions();

}
