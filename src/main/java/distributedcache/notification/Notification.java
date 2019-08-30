package distributedcache.notification;

import java.util.UUID;

/**
 * Represents a {@link Notification} send in case of relevant cache state
 * changes. Each {@link Notification} is identified by a {@link UUID}.
 * 
 * @author Philipp Buchholz
 */
public interface Notification {

	UUID identifier();

	NotificationType type();

	/**
	 * Type of notification which has been published.
	 * 
	 * @author Philipp Buchholz
	 *
	 */
	public enum NotificationType {
		PUT
	}

}
