package distributedcache.cache.notification;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a {@link Notification} send in case of relevant cache state
 * changes. Each {@link Notification} is identified by a {@link UUID}.
 * 
 * @author Philipp Buchholz
 */
public interface Notification<K> {

	/**
	 * Returns the key for which the {@link Notification} is send.
	 */
	K key();

	/**
	 * Returns the value belonging to the key notified. Could be empty because not
	 * all {@link Notification}s require a value alongside the key.
	 * 
	 * @return
	 */
	Optional<Object> value();

	/**
	 * Type of {@link Notification}.
	 * 
	 * @return
	 */
	NotificationType type();

	/**
	 * Type of notification which has been published.
	 * 
	 * @author Philipp Buchholz
	 *
	 */
	public enum NotificationType {
		PUT, //
		UPDATE, //
		DELETE
	}

}
