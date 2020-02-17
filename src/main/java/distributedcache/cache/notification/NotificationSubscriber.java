package distributedcache.cache.notification;

/**
 * Subscribes to {@link Notification}s.
 * 
 * @param <K> The type of key of an Notification.
 * @param <V> The type of value of an Notification.
 * @param <T> The type of subscription supported.
 * 
 * @author Philipp Buchholz
 */
public interface NotificationSubscriber<S extends Subscription, K> {

	/**
	 * Subscribes to the passed in {@link Subscription} of type T and calles the
	 * {@link NotificationListener} in case of incomming Notifications.
	 * 
	 * 
	 * @param subscription The {@link Subscription} to subscribe to.
	 * @param listener     The {@link NotificationListener} handling Notifications.
	 */
	void subscribe(S subscription, NotificationListener<K> listener);

}
