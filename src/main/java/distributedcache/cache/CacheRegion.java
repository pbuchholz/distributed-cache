package distributedcache.cache;

import java.io.Serializable;

/**
 * Represents a Region in a {@link Cache}.
 * 
 * @author Philipp Buchholz
 */
public interface CacheRegion<K extends CacheKey<K>, T extends Serializable> extends Cache<K, T> {

	/**
	 * Returns the name of the {@link CacheRegion}.
	 * 
	 * @return
	 */
	String getRegionName();

}
