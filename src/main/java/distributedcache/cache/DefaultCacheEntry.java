package distributedcache.cache;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents an entry in the cache.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public class DefaultCacheEntry<K extends CacheKey<K>, T extends Serializable> implements CacheEntry<K, T> {

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {

		private CacheEntry<K, T> cacheEntry;

		public Builder() {
			this.cacheEntry = new DefaultCacheEntry<>();
		}

		public Builder<K, T> key(K key) {
			this.cacheEntry.setKey(key);
			return this;
		}

		public Builder<K, T> value(T value) {
			this.cacheEntry.setValue(value);
			return this;
		}

		public Builder<K, T> lastAccess(long lastAccess) {
			this.cacheEntry.setLastAccess(lastAccess);
			return this;
		}

		public Builder<K, T> created(long created) {
			this.cacheEntry.setCreated(created);
			return this;
		}

		public Builder<K, T> validationTimespan(long validationTimespan) {
			this.cacheEntry.setValidationTimespan(validationTimespan);
			return this;
		}

		public CacheEntry<K, T> build() {
			return this.cacheEntry;
		}

	}

	public static <K extends CacheKey<K>, T extends Serializable> CacheEntry<K, T> buildForNow(K key, T value,
			long validationTimespan) {
		return DefaultCacheEntry.<K, T>builder() //
				.key(key) //
				.value(value) //
				.created(System.currentTimeMillis()) //
				.validationTimespan(validationTimespan) //
				.build();
	}

	private K key;
	private T value;

	/* Time control. */
	private long lastAccess;
	private long created = System.currentTimeMillis();
	private long validationTimespan;

	@Override
	public boolean neverAccessed() {
		return 0 == this.lastAccess;
	}

	@Override
	public K key() {
		return this.key;
	}

	@Override
	public T value() {
		return this.value;
	}

	@Override
	public long getLastAccess() {
		return lastAccess;
	}

	@Override
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Override
	public long getCreated() {
		return created;
	}

	@Override
	public void setCreated(long created) {
		this.created = created;
	}

	@Override
	public long getValidationTimespan() {
		return this.validationTimespan;
	}

	@Override
	public void setValidationTimespan(long validationTimespan) {
		this.validationTimespan = validationTimespan;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

}
