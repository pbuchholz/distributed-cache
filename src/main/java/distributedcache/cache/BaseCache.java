package distributedcache.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a basic cache segmented into {@link CacheRegion}s.
 * 
 * A BaseCache is always scoped the the application and qualified as default.
 * 
 * @author Philipp Buchholz
 */
public class BaseCache<K extends CacheKey<K>, T extends Serializable> implements Cache<K, T> {

	private Set<CacheRegion<K, T>> cacheRegions = new CopyOnWriteArraySet<>();

	private CacheConfiguration cacheConfiguration;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(String regionName, K key, T value) {
		CacheEntry<K, T> cacheEntry = CacheEntry.<K, T>builder() //
				.key(key) //
				.value(value) //
				.created(System.currentTimeMillis()) //
				.validationTimespan(cacheConfiguration.validationTimespanForType(value.getClass()).toMillis()) //
				.build();

		CacheRegion<K, T> cacheRegion = Objects.requireNonNull(this.findRegionByName(regionName)) //
				.get();
		cacheRegion.putIntoRegion(cacheEntry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(String regionName, K key) {
		CacheRegion<K, T> cacheRegion = this.findRegionByName(regionName) //
				.get();

		CacheEntry<K, T> cacheEntry = cacheRegion.findInRegion(key);

		if (Objects.isNull(cacheEntry)) {
			return null;
		}

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

		public Builder<K, T> cacheConfiguration(CacheConfiguration cacheConfiguration) {
			baseCache.cacheConfiguration = cacheConfiguration;
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
