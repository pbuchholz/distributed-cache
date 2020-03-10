package distributedcache.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DefaultCacheRegion<K extends CacheKey<K>, T extends Serializable> implements CacheRegion<K, T> {

	private Map<K, CacheEntry<K, T>> cacheEntries = new ConcurrentHashMap<>();
	private String name;

	private DefaultCacheRegion() {
		/* Builder only. */
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {

		private DefaultCacheRegion<K, T> uc;

		public Builder() {
			this.uc = new DefaultCacheRegion<>();
		}

		public Builder<K, T> name(String name) {
			this.uc.name = name;
			return this;
		}

		public Builder<K, T> cacheEntry(CacheEntry<K, T> cacheEntry) {
			uc.cacheEntries.put(cacheEntry.key(), cacheEntry);
			return this;
		}

		public CacheRegion<K, T> build() {
			return this.uc;
		}

	}

	@Override
	public void putIntoRegion(CacheEntry<K, T> cacheEntry) {
		this.cacheEntries.put(cacheEntry.key(), cacheEntry);
	}

	@Override
	public void removeFromRegion(K key) {
		this.cacheEntries.remove(key);
	}

	/**
	 * Flushes the entries of the {@link DefaultCacheRegion} which means that all entries
	 * will be removed from the {@link DefaultCacheRegion}.
	 */
	@Override
	public void flush() {
		this.cacheEntries.clear();
	}

	@Override
	public Collection<CacheEntry<K, T>> cacheEntries() {
		return this.cacheEntries.values();
	}

	@Override
	public CacheEntry<K, T> findInRegion(K key) {
		/* First identify CacheKey using same check. */
		CacheKey<K> cachedKey = this.cacheEntries.keySet().stream() //
				.filter(key::same) //
				.findFirst() //
				.get();

		/* Return CacheEntry from Cache. */
		return this.cacheEntries.get(cachedKey);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
