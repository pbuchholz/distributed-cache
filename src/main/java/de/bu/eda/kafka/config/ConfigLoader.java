package de.bu.eda.kafka.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.resource.spi.IllegalStateException;

public class ConfigLoader {

	private static final String DEFAULT_CONFIGURATION = "application.properties";

	private Properties applicationConfiguration;

	/**
	 * Loads the configuration value for the given {@link InjectionPoint}.
	 * 
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws URISyntaxException
	 */
	@Produces
	@Config
	public String loadConfigurationValue(InjectionPoint injectionPoint)
			throws IOException, IllegalStateException, URISyntaxException {
		if (Objects.isNull(applicationConfiguration)) {
			this.readApplicationConfiguration();
		}

		Config config = injectionPoint.getAnnotated().getAnnotation(Config.class);
		String configKey = config.key();
		if (this.applicationConfiguration.containsKey(configKey)) {
			return this.applicationConfiguration.getProperty(configKey);
		} else if (System.getProperties().containsKey(configKey)) {
			return System.getProperties().getProperty(configKey);
		}

		throw new IllegalStateException("Configuration for key <" //
				.concat(configKey) //
				.concat("> not found."));
	}

	private void readApplicationConfiguration() throws IOException, URISyntaxException {
		String configurationFile = System.getProperty(ConfigLoader.DEFAULT_CONFIGURATION);
		if (Objects.isNull(configurationFile)) {
			configurationFile = ConfigLoader.DEFAULT_CONFIGURATION;
		}

		this.applicationConfiguration = new Properties();
		Path configurationPath = Paths
				.get(Thread.currentThread().getContextClassLoader().getResource(configurationFile).toURI());
		this.applicationConfiguration.load(Files.newInputStream(configurationPath, StandardOpenOption.READ));
	}

}
