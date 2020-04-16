package distributedcache.cache;

import java.io.Serializable;

/**
 * Encapsulates the logic to invalidate a cache according to defined semantics.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public interface CacheInvalidationStrategy<K extends CacheKey<K>, T extends Serializable> {

	/**
	 * Invalidates the passed in {@link Cache} based on the implemented strategy.
	 * 
	 * @param cache
	 */
	void invalidate(Cache<K, T> cache);

}
