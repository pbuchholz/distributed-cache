package distributedcache.cache;

import java.io.Serializable;

import distributedcache.Immutable;

/**
 * CacheEntry interface.
 * 
 * @author Philipp Buchholz
 *
 * @param <K> Type of key.
 * @param <T> Type of value
 */
public interface CacheEntry<K extends CacheKey<K>, T extends Serializable> {

	K key();

	@Immutable
	void setKey(K key);

	T value();

	@Immutable
	void setValue(T value);

	long getLastAccess();

	@Immutable
	void setLastAccess(long lastAccess);

	long getCreated();

	@Immutable
	void setCreated(long created);

	long getValidationTimespan();

	@Immutable
	void setValidationTimespan(long validationTimespan);

	boolean neverAccessed();

}