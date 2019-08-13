package distributedcache.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConfigurationValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private ConfigurationValue configurationValue;

		public Builder() {
			this.configurationValue = new ConfigurationValue();
		}

		public Builder name(String name) {
			this.configurationValue.name = name;
			return this;
		}

		public Builder value(String value) {
			this.configurationValue.value = value;
			return this;
		}

		public ConfigurationValue build() {
			return this.configurationValue;
		}

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
