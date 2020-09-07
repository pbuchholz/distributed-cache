package distributedcache.cache.invalidation;

import java.io.Serializable;

import distributedcache.cache.CacheEntry;
import distributedcache.cache.CacheKey;

/**
 * Invalidates {@link CacheEntry} based on the last access time.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public class LastAccessCacheInvalidationStrategy<K extends CacheKey<K>, T extends Serializable>
		implements InvalidationStrategy<K, T> {

	/**
	 * Invalidation rules:
	 * </ol>
	 * <li>CacheEntry has never been accessed and Creation + ValidTimespan is
	 * smaller</li> than current system time.
	 * <li>CacheEntry has been access but lastAccress + ValidTimespan is smaller
	 * than current system time.</li>
	 * </ol>
	 */
	@Override
	public boolean isInvalid(CacheEntry<K, T> cacheEntry) {
		/* PreConditions. */
		assert 0 != cacheEntry.getCreated();

		if (cacheEntry.neverAccessed()
				&& (cacheEntry.getCreated() + cacheEntry.getValidationTimespan()) > System.currentTimeMillis()) {
			return false;
		}

		if (cacheEntry.getLastAccess() + cacheEntry.getValidationTimespan() > System.currentTimeMillis()) {
			return false;
		}

		return true;
	}
}
