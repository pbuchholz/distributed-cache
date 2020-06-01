package distributedcache.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import distributedcache.ApplicationConfiguration;
import distributedcache.JsonReflector;

/**
 * Stateless bean exposed as REST endpoint to allow retrieval of the current
 * application configuration.
 * 
 * @author Philipp Buchholz
 */
@RestController
@RequestMapping(path = "application-configuration")
public class ApplicationConfigurationBoundary {

	@Autowired
	private ApplicationConfiguration applicationConfiguration;

	@RequestMapping( //
			method = RequestMethod.GET, //
			produces = "application/json" //
	)
	public String getApplicationConfiguration() throws ReflectiveOperationException {
		return JsonReflector.builder() //
				.notTraverseMethod("getMetadata") //
				.build() //
				.buildJsonObject(applicationConfiguration) // ;
				.toString();
	}

}
