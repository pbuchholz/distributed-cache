package distributedcache;

import static distributedcache.cache.configuration.ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION;
import static org.junit.Assert.assertNotNull;

import javax.json.JsonObject;

import org.junit.Test;

import distributedcache.cache.BaseCacheTestBuilder;
import distributedcache.cache.Cache;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;

public class JsonReflectorTest {

	/**
	 * Builds a {@link Cache} instance used for testing.
	 * 
	 * @return
	 */
	public Cache<ConfigurationKey, ConfigurationValue> buildCacheForTesting() {
		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCacheTestBuilder
				.buildDefaultConfiguredBaseCacheWithRootRegion();

		cache.put(ROOT_CONFIGURATION_REGION, new ConfigurationKey("hostname"),
				ConfigurationValue.builder().name("hostname").value("localhost").build());
		cache.put(ROOT_CONFIGURATION_REGION, new ConfigurationKey("post"),
				ConfigurationValue.builder().name("post").value("8080").build());

		cache.put("sub", new ConfigurationKey("invalidationtimespan"),
				ConfigurationValue.builder().name("invalidationtimespan").value("800").build());

		return cache;
	}

	@Test
	public void testBuildReflectively() throws ReflectiveOperationException {
		JsonReflector jsonReflector = JsonReflector.builder() //
				.traverseMethod("getCacheRegions") //
				.traverseMethod("getAll") //
				.build();

		JsonObject cacheJson = jsonReflector.buildJsonObject(this.buildCacheForTesting());

		assertNotNull("CacheRegion has not been marshalled correctly and is null.", cacheJson);
		assertNotNull("Root configuration not found.", cacheJson.getJsonObject(ROOT_CONFIGURATION_REGION));
		assertNotNull("Sub configuration not found.",
				cacheJson.getJsonObject(ROOT_CONFIGURATION_REGION).getJsonObject("sub"));
	}

}
