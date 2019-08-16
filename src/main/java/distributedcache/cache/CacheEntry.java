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
public class CacheEntry<K extends CacheKey<K>, T extends Serializable> {

	public static <K extends CacheKey<K>, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K extends CacheKey<K>, T extends Serializable> {

		private CacheEntry<K, T> cacheEntry;

		public Builder() {
			this.cacheEntry = new CacheEntry<>();
		}

		public Builder<K, T> key(K key) {
			this.cacheEntry.key = key;
			return this;
		}

		public Builder<K, T> value(T value) {
			this.cacheEntry.value = value;
			return this;
		}

		public Builder<K, T> lastAccess(long lastAccess) {
			this.cacheEntry.lastAccess = lastAccess;
			return this;
		}

		public Builder<K, T> created(long created) {
			this.cacheEntry.created = created;
			return this;
		}

		public Builder<K, T> validationTimespan(long validationTimespan) {
			this.cacheEntry.validationTimespan = validationTimespan;
			return this;
		}

		public CacheEntry<K, T> build() {
			return this.cacheEntry;
		}

	}

	private K key;
	private T value;

	/* Time control. */
	private long lastAccess;
	private long created = System.currentTimeMillis();
	private long validationTimespan;

	public boolean neverAccessed() {
		return 0 == this.lastAccess;
	}

	public K key() {
		return this.key;
	}

	public T value() {
		return this.value;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getValidationTimespan() {
		return this.validationTimespan;
	}

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

}
