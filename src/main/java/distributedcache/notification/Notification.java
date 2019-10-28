package distributedcache.notification;

import java.util.UUID;

/**
 * Represents a {@link Notification} send in case of relevant cache state
 * changes. Each {@link Notification} is identified by a {@link UUID}.
 * 
 * @author Philipp Buchholz
 */
public interface Notification {

	/**
	 * Returns the {@link UUID} which uniquely identifies a {@link Notification}.
	 * 
	 * @return {@link UUID} uniquely identifying the {@link Notification}.
	 */
	UUID identifier();

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
