package distributedcache.notification;

/**
 * Implemented by objects which should react on incoming {@link Notification}s.
 * 
 * @author Philipp Buchholz
 */
public interface NotificationListener {

	/**
	 * Calls when a {@link Notification} has been received.
	 * 
	 * @param notification
	 */
	void onNotification(Notification notification);

}
