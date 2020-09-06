package distributedcache.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import distributedcache.Immutable;

/**
 * Defines a cache which is segmented into {@link DefaultCacheRegion}s.
 * 
 * @author Philipp Buchholz
 */
public interface Cache<K extends CacheKey<K>, T extends Serializable> {

	/**
	 * Puts a value into the named region of the cache.
	 * 
	 * @param regionName
	 * @param key
	 * @param value
	 */
	void put(String regionName, K key, T value);

	/**
	 * Puts a value into the cache directly without a specific region.
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, T value);

	/**
	 * Gets a value from a named region by its key out of the cache.
	 * 
	 * @param regionName
	 * @param key
	 */
	T get(String regionName, K key);

	/**
	 * Gets a value directly without a specific region.
	 * 
	 * @param key
	 * @return
	 */
	T get(K key);

	/**
	 * Removes an entry from the cache in a defined region.
	 * 
	 * @param regionName
	 * @param key
	 */
	void removeFromRegion(String regionName, K key);

	/**
	 * Removes an entry from the cache directly.
	 * 
	 * @param key
	 */
	void remove(K key);

	/**
	 * Flushes a named region of the cache.
	 */
	void flushRegion(String regionName);

	/**
	 * Flushes the cache directly without a regard to a specific region.
	 */
	void flush();

	/**
	 * Returns the {@link DefaultCacheRegion} with the passed in name.
	 * 
	 * @param regionName
	 * @return
	 */
	@Immutable
	Cache<K, T> cacheRegionByName(String regionName);

	/**
	 * Returns a unmodifieable {@link Set} of the {@link DefaultCacheRegion}s
	 * available.
	 * 
	 * @return
	 */
	@Immutable
	Map<String, Cache<K, T>> getCacheRegions();

}
