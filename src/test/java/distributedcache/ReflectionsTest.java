package distributedcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import distributedcache.cache.DefaultCacheRegion;

/**
 * Contains tests for {@link Reflections}.
 * 
 * @author Philipp Buchholz
 */
public class ReflectionsTest {

	@Test
	public void testFindImmutableMethods() throws NoSuchMethodException, SecurityException {
		List<Method> immutableMethods = Reflections.immutableMethods(DefaultCacheRegion.class);

		assertNotNull("List of immutable methods is null.", immutableMethods);
		assertEquals("Wrong number of immutable Methods read.", 2, immutableMethods.size());
		assertTrue("cacheEntries is not listed as immutable method.", //
				immutableMethods.stream() //
						.filter(m -> m.getName().equals("cacheEntries")) //
						.findFirst() //
						.isPresent());
		assertTrue("findInRegion is not listed as immutable method.", //
				immutableMethods.stream() //
						.filter(m -> m.getName().equals("findInRegion")) //
						.findFirst() //
						.isPresent());

	}

}
