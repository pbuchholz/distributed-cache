package distributedcache.notification;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Notification published to other Cache instances when a new CacheEntry has
 * been put into another instance.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 * @param <T>
 */
public class PutNotification<K, T extends Serializable> implements Notification, Serializable {

	private static final long serialVersionUID = 8459439634968402296L;

	private UUID identifier;
	private K key;
	private T value;
	private String regionName;

	public static <K, T extends Serializable> Builder<K, T> builder() {
		return new Builder<>();
	}

	public static class Builder<K, T extends Serializable> {
		private PutNotification<K, T> putNotification;

		private Builder() {
			this.putNotification = new PutNotification<>();
		}

		public Builder<K, T> key(K key) {
			this.putNotification.key = key;
			return this;
		}

		public Builder<K, T> value(T value) {
			this.putNotification.value = value;
			return this;
		}

		public Builder<K, T> regionName(String regionName) {
			this.putNotification.regionName = regionName;
			return this;
		}

		public Builder<K, T> identifier(UUID identifier) {
			this.putNotification.identifier = identifier;
			return this;
		}

		public PutNotification<K, T> build() {
			return this.putNotification;
		}
	}

	public K getKey() {
		return key;
	}

	public T getValue() {
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

}
