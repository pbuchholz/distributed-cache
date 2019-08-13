package distributedcache;

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

	void invalidate(Cache<K, T> cache);

}
