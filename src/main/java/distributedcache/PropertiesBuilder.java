package distributedcache;

import java.util.Properties;

/**
 * Convenience class to build {@link Properties}.
 * 
 * @author Philipp Buchholz
 */
public final class PropertiesBuilder {

	private Properties properties;

	public static PropertiesBuilder builder() {
		return new PropertiesBuilder();
	}

	public PropertiesBuilder() {
		this.properties = new Properties();
	}

	public <K, T> PropertiesBuilder put(K key, T value) {
		this.properties.put(key, value);
		return this;
	}

	public Properties build() {
		return this.properties;
	}

}
