package distributedcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Test;

import distributedcache.cache.DefaultCacheEntry;
import distributedcache.cache.CacheRegion;
import distributedcache.cache.DefaultCacheRegion;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;

public class JsonReflectorTest {

	public CacheRegion<ConfigurationKey, ConfigurationValue> buildCacheRegionForTesting() {
		return DefaultCacheRegion.<ConfigurationKey, ConfigurationValue>builder() //
				.name("TestCacheRegion") //
				.cacheEntry(DefaultCacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
						.key(new ConfigurationKey("configuration-key-1")).value(ConfigurationValue.builder() //
								.name("configuration-key-1") //
								.value("configuration-value-1") //
								.build()) //
						.build()) //
				.cacheEntry(DefaultCacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
						.key(new ConfigurationKey("configuration-key-2")).value(ConfigurationValue.builder() //
								.name("configuration-key-2") //
								.value("configuration-value-2") //
								.build()) //
						.build()) //
				.cacheEntry(DefaultCacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
						.key(new ConfigurationKey("configuration-key-3")).value(ConfigurationValue.builder() //
								.name("configuration-key-3") //
								.value("configuration-value-3") //
								.build()) //
						.build()) //
				.build();

	}

	@Test
	public void testBuildReflectively() throws ReflectiveOperationException {
		JsonReflector jsonReflector = JsonReflector.builder() //
				.traverseMethod("cacheEntries") //
				.traverseMethod("value") //
				.traverseMethod("key") //
				.build();

		JsonObject cacheRegionJsonObject = jsonReflector.buildJsonObject(this.buildCacheRegionForTesting());

		assertNotNull("CacheRegion has not been marshalled correctly and is null.", cacheRegionJsonObject);
		assertEquals("TestCacheRegion", cacheRegionJsonObject.getString("name"));

		JsonArray cacheEntriesJsonArray = cacheRegionJsonObject.getJsonArray("cacheentries");
		assertNotNull("CacheEntries has not been marshalled correctly and is null.", cacheEntriesJsonArray);
		assertEquals("Wrong count of CacheEntries found.", 3, cacheEntriesJsonArray.size());
	}

}
