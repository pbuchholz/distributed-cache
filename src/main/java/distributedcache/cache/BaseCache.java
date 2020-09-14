package distributedcache.cache;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
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

	protected Set<CacheRegion<K, T>> cacheRegions;

	protected Map<K, CacheEntry<K, T>> cacheEntries = new ConcurrentHashMap<>();

	protected InvalidationStrategy<K, T> invalidationStrategy;

	protected CacheConfiguration cacheConfiguration;

	protected BaseCache() {
		/* Builder */
	}

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

	private Optional<CacheRegion<K, T>> findRegionByName(String regionName) {
		return flatRegions(this.cacheRegions) //
				.filter(cr -> cr.getRegionName().equals(regionName)) //
				.findFirst();
	}

	/**
	 * Returns a {@link Stream} which contains an {@link Entry} for each CacheRegion
	 * available.
	 * 
	 * @return
	 */
	private Stream<CacheRegion<K, T>> flatRegions(Set<CacheRegion<K, T>> cacheRegions) {
		return Stream.concat(cacheRegions.stream(),
				cacheRegions.stream().flatMap(cr -> flatRegions(cr.getCacheRegions())));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CacheRegion<K, T> cacheRegionByName(String regionName) {
		return (CacheRegion<K, T>) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), //
				new Class[] { CacheRegion.class }, //
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
	public Set<CacheRegion<K, T>> getCacheRegions() {
		return this.cacheRegions;
	}

	public static <K extends CacheKey<K>, T extends Serializable> BaseCacheBuilder<K, T> builder() {
		return new BaseCacheBuilder<K, T>();
	}

	public static class BaseCacheBuilder<K extends CacheKey<K>, T extends Serializable>
			extends Builder<K, T, BaseCacheBuilder<K, T>> {

		@Override
		public Cache<K, T> build() {
			BaseCache<K, T> baseCache = new BaseCache<>();
			baseCache.cacheConfiguration = this.cacheConfiguration;
			baseCache.invalidationStrategy = this.invalidationStrategy;
			baseCache.cacheRegions = this.cacheRegions;
			return baseCache;
		}

		@Override
		protected BaseCacheBuilder<K, T> self() {
			return this;
		}

	}

	public abstract static class Builder<K extends CacheKey<K>, T extends Serializable, B extends Builder<K, T, B>> {
		protected Set<CacheRegion<K, T>> cacheRegions = new CopyOnWriteArraySet<>();
		protected InvalidationStrategy<K, T> invalidationStrategy;
		protected CacheConfiguration cacheConfiguration;

		public B cacheRegion(CacheRegion<K, T> cacheRegion) {
			cacheRegions.add(cacheRegion);
			return self();
		}

		public B cacheRegions(CacheRegion<K, T>... cacheRegions) {
			Stream.of(cacheRegions).forEach(cr -> this.cacheRegions.add(cr));

			return self();
		}

		public B cacheConfiguration(CacheConfiguration cacheConfiguration) {
			this.cacheConfiguration = cacheConfiguration;
			return self();
		}

		public B invalidationStrategy(InvalidationStrategy<K, T> invalidationStrategy) {
			this.invalidationStrategy = invalidationStrategy;
			return self();
		}

		protected abstract B self();

		/**
		 * Builds the {@link Cache} instance.
		 * 
		 * @return The built {@link Cache} instance;
		 */
		protected abstract Cache<K, T> build();

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
