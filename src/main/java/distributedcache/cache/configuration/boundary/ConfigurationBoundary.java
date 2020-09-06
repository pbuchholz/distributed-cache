package distributedcache.cache.configuration.boundary;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import distributedcache.JsonCacheReflectorFactory;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheRegion;
import distributedcache.cache.DefaultCacheEntry;
import distributedcache.cache.DefaultCacheRegion;
import distributedcache.cache.configuration.ConfigurationCache;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;
import distributedcache.cache.invalidation.CacheInvalidationStrategy;
import distributedcache.cache.invalidation.LastAccessCacheInvalidationStrategy;
import distributedcache.cache.notification.DefaultNotification;
import distributedcache.cache.notification.Notification;
import distributedcache.cache.notification.NotificationListener;
import distributedcache.cache.notification.NotificationPublisher;
import distributedcache.cache.notification.NotificationSubscriber;
import distributedcache.cache.notification.kafka.KafkaSubscription;

/**
 * Boundary which connects to Kafka to receive {@link CacheEvent}s to keep
 * caching up to date.
 * 
 * @author Philipp Buchholz
 */
@RestController
@RequestMapping(path = "/configuration")
public class ConfigurationBoundary implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBoundary.class);

	@Autowired
	@ConfigurationCache
	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;

//	@Autowired
//	private Timer cacheInvalidationTimer;

	private CacheInvalidationStrategy<ConfigurationKey, ConfigurationValue> cacheInvalidationStrategy = new LastAccessCacheInvalidationStrategy<>();

	/**
	 * Shouldnt we be able to abstract this to Subscription to be independend from
	 * Kafka in the Boundary?
	 *
	 */
	@Autowired
	private KafkaSubscription<Long, Notification<ConfigurationKey>> subscription;

	@Autowired
	private NotificationSubscriber<KafkaSubscription<Long, Notification<ConfigurationKey>>, ConfigurationKey> subscriber;

	@Autowired
	private NotificationPublisher<KafkaSubscription<Long, Notification<ConfigurationKey>>, ConfigurationKey> publisher;

	/**
	 * Returns a JSON representation of the currently available
	 * {@link DefaultCacheRegion} and their associated {@link DefaultCacheEntry}s.
	 * 
	 * @return
	 * @throws ReflectiveOperationException
	 */
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public String getConfigurationCache() throws ReflectiveOperationException {
		LOGGER.debug("Entering getConfigurationCache.");

		String configurationCache = JsonCacheReflectorFactory //
				.createCacheJsonReflector() //
				.buildJsonObject(this.configurationCache) //
				.toString();
		LOGGER.debug("Exit getConfigurationCache with {}.", configurationCache);

		return configurationCache;
	}

	/**
	 * Adds the given {@link ConfigurationValue} to the {@link DefaultCacheRegion}
	 * with the given name in the configuration cache. This method also publishes an
	 * update of the cache change.
	 * 
	 * @param regionName
	 * @param configurationValue
	 */
	@RequestMapping( //
			path = "{region}", //
			method = RequestMethod.POST, //
			consumes = "application/json")
	public void putCacheEntryAndPublish(@PathVariable("region") String regionName,
			@RequestBody ConfigurationValue configurationValue) {
		LOGGER.debug("Enter putCacheEntryAndPublish with region={}, configurationValue={}.", regionName,
				configurationValue);

		ConfigurationKey configurationKey = new ConfigurationKey(configurationValue.getName());

		LOGGER.trace("putCacheEntryAndPublish -> ConfigurationKey={}.", configurationKey);

		this.configurationCache.put(regionName, configurationKey, configurationValue);

		Notification<ConfigurationKey> notification = DefaultNotification.Builder.<ConfigurationKey>put() //
				.key(configurationKey) //
				.value(configurationValue) //
				.affectedCacheRegion(regionName) //
				.build();

		LOGGER.trace("putCacheEntryAndPublish -> Notification={}.", notification);

		this.publisher.publish(subscription, notification);

		LOGGER.debug("Exit putCacheEntryAndPublish.");
	}

	/**
	 * Deletes the CacheEntry with the key in the passed in {@link CacheRegion}.
	 * This method publishes the delete as delete notification.
	 * 
	 * @param region
	 * @param key
	 */
	@RequestMapping( //
			method = RequestMethod.DELETE, //
			path = "{region}/{key}" //
	)
	public void deleteCacheEntry(@PathVariable("region") String region, @PathVariable("key") String key) {
		assert Objects.nonNull(region);
		assert Objects.nonNull(key);

		CacheRegion<ConfigurationKey, ConfigurationValue> cacheRegion = this.configurationCache
				.cacheRegionByName(region);

		if (Objects.isNull(cacheRegion)) {
			Response.status(Status.NOT_FOUND.getStatusCode(), String.format(
					"CacheRegion %s could not be found in ConfigurationCache. ConfigurationKey could not be deleted.",
					region));
		}

		ConfigurationKey configurationKey = new ConfigurationKey(key);
		ConfigurationValue configurationValue = cacheRegion.findInRegion(configurationKey).value();
		cacheRegion.removeFromRegion(configurationKey);

		Notification<ConfigurationKey> notification = DefaultNotification.Builder.<ConfigurationKey>put() //
				.key(configurationKey) //
				.value(configurationValue) //
				.affectedCacheRegion(region) //
				.build();

		this.publisher.publish(subscription, notification);

	}

	@PostConstruct
	public void startup() {

		/* Subscribe to subscription. */
		this.subscriber.subscribe(this.subscription, new NotificationListener<ConfigurationKey>() {

			@Override
			public void onNotification(Notification<ConfigurationKey> notification) {

				switch (notification.type()) {

				case PUT:
				case UPDATE:
					configurationCache.put(notification.affectedCacheRegion(), notification.key(),
							(ConfigurationValue) notification.value().get());
					break;
				case DELETE:
					CacheRegion<ConfigurationKey, ?> cacheRegion = configurationCache
							.cacheRegionByName(notification.affectedCacheRegion());
					if (Objects.isNull(cacheRegion)) {
						throw new RuntimeException(String.format("CacheRegion %s is null. Cannot process notification.",
								notification.affectedCacheRegion()));
					}
					cacheRegion.removeFromRegion(notification.key());
				}

				/* Publish notification further to peer. */
				publisher.publish(subscription, notification);
			}
		});
	}

	// TODO Must be scheduled using the configured invalidation period from
	// ApplicationConfiguration.
	@Scheduled(fixedRate = 200)
	public void invalidation() {
		/* Invalidate cache according to strategy. */
		cacheInvalidationStrategy.invalidate(this.configurationCache);
	}

//	public void releaseCache() {
//		this.cacheInvalidationTimer.cancel();
//	}

}
