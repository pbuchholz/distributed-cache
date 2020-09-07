package distributedcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import distributedcache.cache.BaseCache;

/**
 * Contains tests for {@link Reflections}.
 * 
 * @author Philipp Buchholz
 */
public class ReflectionsTest {

	@Test
	public void testFindImmutableMethods() throws NoSuchMethodException, SecurityException {
		List<Method> immutableMethods = Reflections.immutableMethods(BaseCache.class);

		assertNotNull("List of immutable methods is null.", immutableMethods);
		assertEquals("Wrong number of immutable Methods read.", 3, immutableMethods.size());
		assertTrue("Method [cacheRegionByName] is immutable.", //
				immutableMethods.stream() //
						.filter(m -> m.getName().equals("cacheRegionByName")) //
						.findFirst() //
						.isPresent());
		assertTrue("Method [getCacheRegions] is immutable", //
				immutableMethods.stream() //
						.filter(m -> m.getName().equals("getCacheRegions")) //
						.findFirst() //
						.isPresent());
		assertTrue("Method [getAll] is immutable", //
				immutableMethods.stream() //
						.filter(m -> m.getName().equals("getAll")) //
						.findFirst() //
						.isPresent());

	}

}
