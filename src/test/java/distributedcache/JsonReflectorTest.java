package distributedcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Test;

import distributedcache.cache.BaseCache;
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
		Cache<ConfigurationKey, ConfigurationValue> cache = BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
				.cacheRegion("root", BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
						.cacheRegion("sub", BaseCache.<ConfigurationKey, ConfigurationValue>builder() //
								.build()) //
						.build()) //
				.build();

		cache.put("root", new ConfigurationKey("hostname"),
				ConfigurationValue.builder().name("hostname").value("localhost").build());
		cache.put("root", new ConfigurationKey("post"),
				ConfigurationValue.builder().name("post").value("8080").build());

		cache.put("sub", new ConfigurationKey("invalidationtimespan"),
				ConfigurationValue.builder().name("invalidationtimespan").value("800").build());

		return cache;
	}

	@Test
	public void testBuildReflectively() throws ReflectiveOperationException {
		JsonReflector jsonReflector = JsonReflector.builder() //
				.traverseMethod("getCacheRegions") //
				.build();

		JsonObject cacheRegionJsonObject = jsonReflector.buildJsonObject(this.buildCacheForTesting());

		assertNotNull("CacheRegion has not been marshalled correctly and is null.", cacheRegionJsonObject);
		assertEquals("TestCacheRegion", cacheRegionJsonObject.getString("name"));

		JsonArray cacheEntriesJsonArray = cacheRegionJsonObject.getJsonArray("cacheentries");
		assertNotNull("CacheEntries has not been marshalled correctly and is null.", cacheEntriesJsonArray);
		assertEquals("Wrong count of CacheEntries found.", 3, cacheEntriesJsonArray.size());
	}

}
