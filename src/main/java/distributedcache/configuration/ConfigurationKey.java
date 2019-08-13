package distributedcache.configuration;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import distributedcache.CacheKey;

public class ConfigurationKey implements CacheKey<ConfigurationKey> {

	private static final long serialVersionUID = -9069066599344394724L;

	private String configurationValueName;

	public ConfigurationKey(String configurationValueName) {
		this.configurationValueName = configurationValueName;
	}

	public String getConfigurationValueName() {
		return this.configurationValueName;
	}

	@Override
	public boolean same(ConfigurationKey other) {
		return other.configurationValueName.equals(configurationValueName);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
