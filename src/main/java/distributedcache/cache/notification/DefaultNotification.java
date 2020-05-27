package distributedcache.cache.notification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

/**
 * Default implementation of {@link Notification}.
 * 
 * @author Philipp Buchholz
 *
 * @param <K>
 */
public class DefaultNotification<K> implements Notification<K> {

	private static final long serialVersionUID = 1L;

	private K key;
	private transient Optional<Object> value;
	private NotificationType notificationType;
	private String affectedCacheRegion;

	@Override
	public K key() {
		return this.key;
	}

	@Override
	public Optional<Object> value() {
		return this.value;
	}

	@Override
	public NotificationType type() {
		return this.notificationType;
	}

	@Override
	public String affectedCacheRegion() {
		return this.affectedCacheRegion;
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		if (value.isPresent()) {
			objectOutputStream.writeObject(value.get());
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
		objectInputStream.defaultReadObject();
		Object value = objectInputStream.readObject();
		if (value != null) {
			this.value = Optional.of(value);
		}

	}

	public static class Builder<K> {
		private DefaultNotification<K> notification;

		private Builder() {
			/* Use static factory methods. */
		}

		public static <K> Builder<K> put() {
			Builder<K> builder = new Builder<>();
			builder.notification = new DefaultNotification<>();
			builder.notification.notificationType = NotificationType.PUT;
			return builder;
		}

		public static <K> Builder<K> delete() {
			Builder<K> builder = new Builder<>();
			builder.notification = new DefaultNotification<>();
			builder.notification.notificationType = NotificationType.DELETE;
			return builder;
		}

		public static <K> Builder<K> update() {
			Builder<K> builder = new Builder<>();
			builder.notification = new DefaultNotification<>();
			builder.notification.notificationType = NotificationType.UPDATE;
			return builder;
		}

		public Builder<K> key(K key) {
			this.notification.key = key;
			return this;
		}

		public Builder<K> value(Object value) {
			this.notification.value = Optional.of(value);
			return this;
		}

		public Builder<K> affectedCacheRegion(String affectedCacheRegion) {
			this.notification.affectedCacheRegion = affectedCacheRegion;
			return this;
		}

		public DefaultNotification<K> build() {
			return this.notification;
		}
	}

}
