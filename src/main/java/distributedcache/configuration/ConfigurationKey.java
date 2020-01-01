package distributedcache.configuration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import distributedcache.cache.CacheKey;

/**
 * Uniquely identifies a ConfigurationValue stored in a Cache.
 * 
 * @author Philipp Buchholz
 */
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

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other, false);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(this.configurationValueName) //
				.build();
	}

}
