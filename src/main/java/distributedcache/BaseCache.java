package distributedcache;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import distributedcache.notification.NotificationListener;
import distributedcache.notification.PutNotification;

/**
 * Represents a basic cache segmented into {@link CacheRegion}s.
 * 
 * A BaseCache is always scoped the the application and qualified as default.
 * 
 * @author Philipp Buchholz
 */
public class BaseCache<K extends CacheKey<K>, T extends Serializable> implements Cache<K, T> {

	private Set<CacheRegion<K, T>> cacheRegions = new CopyOnWriteArraySet<>();

	private Set<NotificationListener> notificationListeners = Collections.synchronizedSet(new HashSet<>());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(String regionName, CacheEntry<K, T> cacheEntry) {
		CacheRegion<K, T> cacheRegion = Objects.requireNonNull(this.findRegionByName(regionName)) //
				.get();
		cacheRegion.putIntoRegion(cacheEntry);

		notificationListeners.forEach(l -> l.onNotification(PutNotification.builder() //
				.identifier(UUID.randomUUID()) //
				.cacheKey(cacheEntry.key()) //
				.value(cacheEntry.value()) //
				.regionName(regionName) //
				.build()));
	}

	@Override
	public void registerNotificationListener(NotificationListener notificationListener) {
		this.notificationListeners.add(notificationListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(String regionName, K key) {
		CacheRegion<K, T> cacheRegion = this.findRegionByName(regionName) //
				.get();

		CacheEntry<K, T> cacheEntry = Objects.requireNonNull(cacheRegion.findInRegion(key));

		cacheEntry.setLastAccess(System.currentTimeMillis());
		return cacheEntry.value();
	}

	private Optional<CacheRegion<K, T>> findRegionByName(String regionName) {
		return cacheRegions.stream() //
				.filter((cr) -> cr.getName().equals(regionName)) //
				.findFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flushRegion(String regionName) {
		this.findRegionByName(regionName) //
				.get() //
				.flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CacheRegion<K, T>> getCacheRegions() {
		return Collections.unmodifiableSet(this.cacheRegions);
	}

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {
		private BaseCache<K, T> baseCache;

		public Builder() {
			this.baseCache = new BaseCache<>();
		}

		public Builder<K, T> cacheRegion(String regionName) {
			baseCache.cacheRegions.add(CacheRegion.<K, T>builder() //
					.name(regionName) //
					.build());
			return this;
		}

		public BaseCache<K, T> build() {
			return this.baseCache;
		}

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
