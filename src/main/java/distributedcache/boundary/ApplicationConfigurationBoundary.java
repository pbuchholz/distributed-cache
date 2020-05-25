package distributedcache.boundary;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import distributedcache.ApplicationConfiguration;
import distributedcache.JsonReflector;

/**
 * Stateless bean exposed as REST endpoint to allow retrieval of the current
 * application configuration.
 * 
 * @author Philipp Buchholz
 */
@Stateless
@Path("application-configuration")
public class ApplicationConfigurationBoundary {

	@Inject
	private ApplicationConfiguration applicationConfiguration;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getApplicationConfiguration() throws ReflectiveOperationException {
		return JsonReflector.builder() //
				.notTraverseMethod("getMetadata") //
				.build() //
				.buildJsonObject(applicationConfiguration) // ;
				.toString();
	}

}
