package distributedcache;

/**
 * Factory which creates instances of {@link JsonReflector}.
 * 
 * @author Philipp Buchholz
 */
public final class JsonReflectorFactory {

	private JsonReflectorFactory() {

	}

	/**
	 * Creates a {@link JsonReflector} configured for Cache instances.
	 * 
	 * @return
	 */
	public static JsonReflector createCacheJsonReflector() {
		return JsonReflector.builder() //
				.traverseMethod("cacheEntries") //
				.traverseMethod("value") //
				.traverseMethod("key") //
				.build();
	}

}
