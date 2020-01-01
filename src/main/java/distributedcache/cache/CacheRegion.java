package distributedcache.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CacheRegion<K extends CacheKey<K>, T extends Serializable> {

	private Map<K, CacheEntry<K, T>> cacheEntries = new ConcurrentHashMap<>();
	private String name;

	private CacheRegion() {
		/* Builder only. */
	}

	public String getName() {
		return this.name;
	}

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {

		private CacheRegion<K, T> uc;

		public Builder() {
			this.uc = new CacheRegion<>();
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

	public void putIntoRegion(CacheEntry<K, T> cacheEntry) {
		this.cacheEntries.put(cacheEntry.key(), cacheEntry);
	}

	public void removeFromRegion(K key) {
		this.cacheEntries.remove(key);
	}

	/**
	 * Flushes the entries of the {@link CacheRegion} which means that all entries
	 * will be removed from the {@link CacheRegion}.
	 */
	public void flush() {
		this.cacheEntries.clear();
	}

	public Collection<CacheEntry<K, T>> cacheEntries() {
		return this.cacheEntries.values();
	}

	public CacheEntry<K, T> findInRegion(K key) {
		/* First identify CacheKey using same check. */
		CacheKey<K> cachedKey = this.cacheEntries.keySet().stream() //
				.filter(k -> k.same(key)) //
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
