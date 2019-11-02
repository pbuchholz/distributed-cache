package distributedcache;

import javax.json.JsonObject;

import org.junit.Test;

import distributedcache.cache.CacheEntry;
import distributedcache.cache.CacheRegion;
import distributedcache.configuration.ConfigurationKey;
import distributedcache.configuration.ConfigurationValue;

public class JsonReflectorTest {

	public CacheRegion<ConfigurationKey, ConfigurationValue> buildCacheRegionForTesting() {
		return CacheRegion.<ConfigurationKey, ConfigurationValue>builder() //
				.name("TestCacheRegion") //
				.cacheEntry(CacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
						.key(new ConfigurationKey("configuration-key-1")).value(ConfigurationValue.builder() //
								.name("configuration-key-1") //
								.value("configuration-value-1") //
								.build()) //
						.build()) //
				.cacheEntry(CacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
						.key(new ConfigurationKey("configuration-key-2")).value(ConfigurationValue.builder() //
								.name("configuration-key-2") //
								.value("configuration-value-2") //
								.build()) //
						.build()) //
				.cacheEntry(CacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
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
				.build();

		JsonObject cacheRegionJsonObject = jsonReflector.buildJsonObject(this.buildCacheRegionForTesting());
		System.out.println(cacheRegionJsonObject.toString());
	}

}
