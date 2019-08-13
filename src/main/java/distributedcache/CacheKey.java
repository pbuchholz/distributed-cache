package distributedcache;

import java.io.Serializable;

/**
 * Represents a Key to access an entry in the {@link BaseCache}.
 * 
 * @author Philipp Buchholz
 */
public interface CacheKey<K extends CacheKey<K>> extends Serializable {

	boolean same(K other);

}
