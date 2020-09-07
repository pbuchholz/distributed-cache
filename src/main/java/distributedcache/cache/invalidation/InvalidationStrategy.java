package distributedcache.cache.invalidation;

import java.io.Serializable;

import distributedcache.cache.Cache;
import distributedcache.cache.CacheEntry;
import distributedcache.cache.CacheKey;

/**
 * Encapsulates the logic to invalidate a cache according to defined semantics.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public interface InvalidationStrategy<K extends CacheKey<K>, T extends Serializable> {

	public final class None<K extends CacheKey<K>, T extends Serializable> implements InvalidationStrategy<K, T> {

		@Override
		public boolean isInvalid(CacheEntry<K, T> cacheEntry) {
			return false;
		}

	}

	/**
	 * Invalidates the passed in {@link Cache} based on the implemented strategy.
	 * 
	 * @return <code>true</code> if the passed in {@link CacheEntry} is invalid.
	 * @param cache
	 */
	boolean isInvalid(CacheEntry<K, T> cacheEntry);

}
