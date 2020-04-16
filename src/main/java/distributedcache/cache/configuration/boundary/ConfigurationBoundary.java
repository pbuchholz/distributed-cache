package distributedcache.cache.configuration.boundary;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import distributedcache.JsonCacheReflectorFactory;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheManager;
import distributedcache.cache.DefaultCacheEntry;
import distributedcache.cache.DefaultCacheRegion;
import distributedcache.cache.configuration.ConfigurationCache;
import distributedcache.cache.configuration.ConfigurationCacheProvider;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;
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
@Path("configuration")
@ApplicationScoped
public class ConfigurationBoundary implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBoundary.class);

	@Inject
	@ConfigurationCache
	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;

	@Inject
	private CacheManager<ConfigurationKey, ConfigurationValue> configurationCacheManager;

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
	 * {@link DefaultCacheRegion}ØÏ and their associated {@link DefaultCacheEntry}s.
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
				.build();

		this.publisher.publish(this.subscription, notification);
	}

	@PostConstruct
	public void startup() {

		/* Initialize CacheManager for ConfigurationCache. */
		this.configurationCacheManager.manageCache(this.configurationCache);

		/* Subscribe to subscription. */
		this.subscriber.subscribe(this.subscription, new NotificationListener<ConfigurationKey>() {

			@Override
			public void onNotification(Notification<ConfigurationKey> notification) {
				// TODO we have to differentiate the different types of notifications here.
				configurationCache.put(ConfigurationCacheProvider.ROOT_CONFIGURATION_REGION, notification.key(),
						(ConfigurationValue) notification.value().get());

			}
		});
	}

	@PreDestroy
	public void shutdown() {
		/* Shutdown CacheManager. */
		this.configurationCacheManager.releaseCache();
	}

}
