package distributedcache.cache;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import distributedcache.ImmutableInvocationHandler;
import distributedcache.cache.invalidation.InvalidationStrategy;

/**
 * Represents a basic cache segmented into CacheRegions. The CacheRegions itself
 * are represented as {@link BaseCache} instances.
 * 
 * @author Philipp Buchholz
 */
public class BaseCache<K extends CacheKey<K>, T extends Serializable> implements Cache<K, T> {

	private Map<String, Cache<K, T>> cacheRegions = new ConcurrentHashMap<>();

	private Map<K, CacheEntry<K, T>> cacheEntries = new ConcurrentHashMap<>();

	private InvalidationStrategy<K, T> invalidationStrategy;

	private CacheConfiguration cacheConfiguration;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(String regionName, K key, T value) {
		Cache<K, T> cacheRegion = Objects.requireNonNull(this.findRegionByName(regionName)) //
				.get();
		cacheRegion.put(key, value);
	}

	@Override
	public void put(K key, T value) {
		CacheEntry<K, T> cacheEntry = DefaultCacheEntry.buildForNow(key, value,
				cacheConfiguration.validationTimespanForType(value.getClass()).toMillis());
		this.cacheEntries.put(key, cacheEntry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(String regionName, K key) {
		Cache<K, T> cacheRegion = this.findRegionByName(regionName) //
				.get();
		return cacheRegion.get(key);
	}

	@Override
	public T get(K key) {
		/* First identify CacheKey using same check. */
		CacheKey<K> cachedKey = this.findCacheKey(key);

		/* Return CacheEntry from Cache. */
		CacheEntry<K, T> cacheEntry = Objects.requireNonNull(this.cacheEntries.get(cachedKey));
		cacheEntry.setLastAccess(System.currentTimeMillis());
		return cacheEntry.value();
	}

	@Override
	public Collection<T> getAll() {
		return this.cacheEntries.values().stream() //
				.map(e -> e.value()) //
				.collect(Collectors.toList());
	}

	private CacheKey<K> findCacheKey(K key) {
		/* First identify CacheKey using same check. */
		return this.cacheEntries.keySet().stream() //
				.filter(key::same) //
				.findFirst() //
				.get();
	}

	private Optional<Cache<K, T>> findRegionByName(String regionName) {
		return flatRegions(this.cacheRegions) //
				.filter(cr -> cr.getKey().equals(regionName)) //
				.map(e -> e.getValue()) //
				.findFirst();
	}

	/**
	 * Returns a {@link Stream} which contains an {@link Entry} for each CacheRegion
	 * available.
	 * 
	 * @return
	 */
	private Stream<Entry<String, Cache<K, T>>> flatRegions(Map<String, Cache<K, T>> cacheRegions) {
		return Stream.concat(cacheRegions.entrySet().stream(),
				cacheRegions.entrySet().stream().flatMap(e -> flatRegions(e.getValue().getCacheRegions())));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cache<K, T> cacheRegionByName(String regionName) {
		return (Cache<K, T>) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), //
				new Class[] { Cache.class }, //
				new ImmutableInvocationHandler(this.findRegionByName(regionName) //
						.get()));
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
	public void flush() {
		this.cacheEntries.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Cache<K, T>> getCacheRegions() {
		return this.cacheRegions;
	}

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {
		private BaseCache<K, T> baseCache;

		public Builder() {
			this.baseCache = new BaseCache<>();
		}

		public Builder<K, T> cacheRegion(String regionName, Cache<K, T> cacheRegion) {
			baseCache.cacheRegions.put(regionName, cacheRegion);
			return this;
		}

		public Builder<K, T> cacheConfiguration(CacheConfiguration cacheConfiguration) {
			baseCache.cacheConfiguration = cacheConfiguration;
			return this;
		}

		public Builder<K, T> invalidationStrategy(InvalidationStrategy<K, T> invalidationStrategy) {
			this.baseCache.invalidationStrategy = invalidationStrategy;
			return this;
		}

		public Cache<K, T> build() {
			return this.baseCache;
		}

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public void remove(String regionName, K key) {
		this.findRegionByName(regionName) //
				.get() //
				.remove(key);
	}

	@Override
	public void remove(K key) {
		CacheKey<K> cacheKey = this.findCacheKey(key);
		this.cacheEntries.remove(cacheKey);
	}

	/**
	 * Invalidates the entries of the {@link Cache}.
	 */
	@Override
	public void invalidate() {
		this.cacheEntries.entrySet().stream() //
				.filter(e -> this.invalidationStrategy.isInvalid(e.getValue())) //
				.map(Entry::getKey) //
				.forEach(k -> this.cacheEntries.remove(k));
	}

}
