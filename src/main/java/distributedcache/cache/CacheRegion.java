package distributedcache.cache;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a region in a cache.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public interface CacheRegion<K extends CacheKey<K>, T extends Serializable> {

	/**
	 * Name of the {@link CacheRegion}.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Puts a new {@link CacheEntry} into the {@link CacheRegion}.
	 * 
	 * @param cacheEntry
	 */
	void putIntoRegion(CacheEntry<K, T> cacheEntry);

	/**
	 * Removes a {@link CacheEntry} from the {@link CacheRegion} by its key.
	 * 
	 * @param key
	 */
	void removeFromRegion(K key);

	/**
	 * Flushes the entries of the {@link DefaultCacheRegion} which means that all
	 * entries will be removed from the {@link DefaultCacheRegion}.
	 */
	void flush();

	/**
	 * Returns the {@link CacheEntry}s currently available in the
	 * {@link CacheRegion}.
	 * 
	 * @return
	 */
	Collection<CacheEntry<K, T>> cacheEntries();

	/**
	 * Finds a {@link CacheEntry} in the {@link CacheRegion} by its key.
	 * 
	 * @param key
	 * @return
	 */
	CacheEntry<K, T> findInRegion(K key);

}