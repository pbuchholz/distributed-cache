package distributedcache.cache;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Manages the lifecycle of a single {@link Cache} instance by providing the
 * following lifecylce services.
 * 
 * <ul>
 * <li>Starting a {@link Timer} using {@link TimerService} to provide cache
 * invalidation in fixed intervals.</li>
 * <li></li>
 * </ul>
 * 
 * @author Philipp Buchholz
 */
@Dependent
public class CacheManager<K extends CacheKey<K>, T extends Serializable> {

	@Resource
	private TimerService timerService;

	private Timer cacheInvalidationTimer;

	@Inject
	private CacheInvalidationStrategy<K, T> cacheInvalidationStrategy;

	private Cache<K, T> managedCache;

	public void manageCache(Cache<K, T> managedCache) {
		this.managedCache = managedCache;
	}

	@Schedule
	private void invalidation() {
		/* Invalidate cache according to strategy. */
		cacheInvalidationStrategy.invalidate(this.managedCache);
	}

	public void releaseCache() {
		this.cacheInvalidationTimer.cancel();
	}

}
