package distributedcache.notification;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import distributedcache.CacheKey;

public class PutNotification implements Notification, Serializable {

	private static final long serialVersionUID = 8459439634968402296L;

	private UUID identifier;
	private CacheKey<?> cacheKey;
	private Serializable value;
	private String regionName;
	private int source;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private PutNotification putNotification;

		private Builder() {
			this.putNotification = new PutNotification();
		}

		public Builder cacheKey(CacheKey<?> cacheKey) {
			this.putNotification.cacheKey = cacheKey;
			return this;
		}

		public Builder value(Serializable value) {
			this.putNotification.value = value;
			return this;
		}

		public Builder regionName(String regionName) {
			this.putNotification.regionName = regionName;
			return this;
		}

		public Builder identifier(UUID identifier) {
			this.putNotification.identifier = identifier;
			return this;
		}

		public PutNotification build() {
			return this.putNotification;
		}
	}

	public CacheKey<?> getCacheKey() {
		return cacheKey;
	}

	public Serializable getValue() {
		return value;
	}

	public String getRegionName() {
		return regionName;
	}

	@Override
	public UUID identifier() {
		return this.identifier;
	}

	@Override
	public NotificationType type() {
		return NotificationType.PUT;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public void setSource(int source) {
		this.source = source;
	}

	@Override
	public int getSource() {
		return this.source;
	}

}
