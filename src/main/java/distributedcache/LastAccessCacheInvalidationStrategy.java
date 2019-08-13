package distributedcache;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

@Default
@Dependent
public class LastAccessCacheInvalidationStrategy<K extends CacheKey<K>, T extends Serializable>
		implements CacheInvalidationStrategy<K, T> {

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
	public void invalidate(Cache<K, T> cache) {
		cache.getCacheRegions().forEach((cr) -> {
			cr.cacheEntries().forEach((ce) -> {

				/* PreConditions. */
				assert 0 != ce.getCreated();

				if ((ce.neverAccessed() && (ce.getCreated() + ce.getValidationTimespan()) > System.currentTimeMillis()) //
						|| (ce.getLastAccess() + ce.getValidationTimespan() > System.currentTimeMillis())) {
					return;
				}

				cr.removeFromRegion(ce.key());
			});
		});

	}

}
