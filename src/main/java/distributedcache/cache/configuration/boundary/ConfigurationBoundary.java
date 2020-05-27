package distributedcache.cache.configuration.boundary;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Stateless
@Path("configuration")
public class ConfigurationBoundary implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBoundary.class);

	@Inject
	@ConfigurationCache
	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;

	@Resource
	private TimerService timerService;

	private Timer cacheInvalidationTimer;

	private CacheInvalidationStrategy<ConfigurationKey, ConfigurationValue> cacheInvalidationStrategy = new LastAccessCacheInvalidationStrategy<>();

	/**
	 * Shouldnt we be able to abstract this to Subscription to be independend from
	 * Kafka in the Boundary?
	 *
	 */
	@Inject
	private KafkaSubscription<Long, Notification<ConfigurationKey>> subscription;

	@Inject
	private NotificationSubscriber<KafkaSubscription<Long, Notification<ConfigurationKey>>, ConfigurationKey> subscriber;

	@Inject
	private NotificationPublisher<KafkaSubscription<Long, Notification<ConfigurationKey>>, ConfigurationKey> publisher;

	/**
	 * Returns a JSON representation of the currently available
	 * {@link DefaultCacheRegion} and their associated {@link DefaultCacheEntry}s.
	 * 
	 * @return
	 * @throws ReflectiveOperationException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfigurationCache() throws ReflectiveOperationException {
		return JsonCacheReflectorFactory //
				.createCacheJsonReflector() //
				.buildJsonObject(this.configurationCache) //
				.toString();
	}

	/**
	 * Adds the given {@link ConfigurationValue} to the {@link DefaultCacheRegion}
	 * with the given name in the configuration cache. This method also publishes an
	 * update of the cache change.
	 * 
	 * @param regionName
	 * @param configurationValue
	 */
	@POST
	@Path("{region}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putCacheEntryAndPublish(@PathParam("region") String regionName, ConfigurationValue configurationValue) {
		ConfigurationKey configurationKey = new ConfigurationKey(configurationValue.getName());

		this.configurationCache.put(regionName, configurationKey, configurationValue);

		Notification<ConfigurationKey> notification = DefaultNotification.Builder.<ConfigurationKey>put() //
				.key(configurationKey) //
				.value(configurationValue) //
				.affectedCacheRegion(regionName) //
				.build();

		this.publisher.publish(subscription, notification);
	}

	/**
	 * Deletes the CacheEntry with the key in the passed in {@link CacheRegion}.
	 * This method publishes the delete as delete notification.
	 * 
	 * @param region
	 * @param key
	 */
	@DELETE
	@Path("{region}/{key}")
	public void deleteCacheEntry(@PathParam("region") String region, @PathParam("key") String key) {
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

	// @Schedule
	public void invalidation() {
		/* Invalidate cache according to strategy. */
		cacheInvalidationStrategy.invalidate(this.configurationCache);
	}

	public void releaseCache() {
		this.cacheInvalidationTimer.cancel();
	}

}
