package distributedcache.cache;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import distributedcache.ApplicationConfiguration;

/**
 * Manages {@link Cache} instances by providing:
 * 
 * <ul>
 * <li>Starting a {@link Timer} using {@link TimerService} to provide cache
 * invalidation in fixed intervals</li>
 * </ul>
 * 
 * @author Philipp Buchholz
 */
@ApplicationScoped
public class CacheManager<K extends CacheKey<K>, T extends Serializable> {

	@Resource
	private TimerService timerService;

	private Timer cacheInvalidationTimer;

	@Inject
	private CacheInvalidationStrategy<K, T> cacheInvalidationStrategy;

	@Inject
	private ApplicationConfiguration applicationConfiguration;

	private Cache<K, T> managedCache;

	public void manageCache(Cache<K, T> managedCache) {
		this.managedCache = managedCache;
		this.cacheInvalidationTimer = timerService.createIntervalTimer(
				this.applicationConfiguration.getCacheInvalidationPeriod(),
				this.applicationConfiguration.getCacheInvalidationPeriod(), new TimerConfig());
	}

	@PreDestroy
	public void shutdown() {
		this.cacheInvalidationTimer.cancel();
	}

	@Schedule
	public void invalidation() {
		cacheInvalidationStrategy.invalidate(this.managedCache);
	}

}
