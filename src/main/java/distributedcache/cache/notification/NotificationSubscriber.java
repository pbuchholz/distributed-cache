package distributedcache.cache.notification;

/**
 * Subscribes to {@link Notification}s.
 * 
 * @param <S> The type of {@link Subscription} supported.
 * @param <K> The type of {@link Notification} Key supported.
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
