package distributedcache.cache.notification;

/**
 * Listens for Notifications and processes them.
 * 
 * @author Philipp Buchholz
 */
public interface NotificationListener<K> {

	/**
	 * Called uppon an incomming {@link Notification}.
	 * 
	 * @param notification
	 */
	void onNotification(Notification<K> notification);

}