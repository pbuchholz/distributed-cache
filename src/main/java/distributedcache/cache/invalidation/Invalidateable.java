package distributedcache.cache.invalidation;

/**
 * Implemented by objects which can be invalidated.
 * 
 * @author Philipp Buchholz
 */
public interface Invalidateable {

	/**
	 * Invalidate the object.
	 */
	void invalidate();

}
