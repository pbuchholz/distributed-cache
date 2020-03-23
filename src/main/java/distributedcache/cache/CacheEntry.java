package distributedcache.cache;

import java.io.Serializable;

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

	void setKey(K key);

	T value();

	void setValue(T value);

	long getLastAccess();

	void setLastAccess(long lastAccess);

	long getCreated();

	void setCreated(long created);

	long getValidationTimespan();

	void setValidationTimespan(long validationTimespan);

	boolean neverAccessed();

}