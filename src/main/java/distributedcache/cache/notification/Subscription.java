package distributedcache.cache.notification;

import distributedcache.cache.notification.kafka.Topic;

/**
 * Describes where to subscribe for {@link Notification}s.
 * 
 * @author Philipp Buchholz
 */
public interface Subscription {

	/**
	 * The {@link Topic} used for incomming messages.
	 * 
	 * @return
	 */
	Topic inTopic();

	/**
	 * The {@link Topic} used for outgoing messages.
	 * 
	 * @return
	 */
	Topic outTopic();

	/**
	 * The {@link Topic} used for failing messages.
	 * 
	 * @return
	 */
	Topic failTopic();

}
