package distributedcache.cache.notification;

/**
 * Publishes {@link Notification}s over a {@link Subscription}.
 * 
 * @author Philipp Buchholz
 *
 * @param <S> Type of {@link Subscription} supported.
 * @param <K> Type of {@link Notification} Key supported.
 */
public interface NotificationPublisher<S extends Subscription, K> {

	/**
	 * Subscribes to the passed in {@link Subscription} of type T and is able to
	 * publish {@link Notification}s.
	 * 
	 * 
	 * @param subscription The {@link Subscription} to subscribe to.
	 * @param notification The {@link Notification} published.
	 */
	void publish(S subscription, Notification<K> notification);

}
