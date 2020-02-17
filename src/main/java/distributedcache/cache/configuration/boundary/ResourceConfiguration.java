package distributedcache.cache.configuration.boundary;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class ResourceConfiguration extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resourceClasses = new HashSet<>();
		resourceClasses.add(ConfigurationBoundary.class);
		return resourceClasses;
	}

}
