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
import distributedcache.cache.CacheEntry;
import distributedcache.cache.CacheManager;
import distributedcache.cache.CacheRegion;
import distributedcache.cache.configuration.ConfigurationCache;
import distributedcache.cache.configuration.ConfigurationKey;
import distributedcache.cache.configuration.ConfigurationValue;
import distributedcache.cache.notification.Notification;
import distributedcache.cache.notification.NotificationListener;
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

//	@Inject
//	private ApplicationConfiguration configuration;

	@Inject
	private KafkaSubscription<Long, Notification<ConfigurationKey>> subscription;

	@Inject
	private NotificationSubscriber<KafkaSubscription<Long, Notification<ConfigurationKey>>, Notification<ConfigurationKey>> subscriber;

	/**
	 * Returns a JSON representation of the currently available
	 * {@link CacheRegion}ØÏ and their associated {@link CacheEntry}s.
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
	 * Adds the given {@link ConfigurationValue} to the {@link CacheRegion} with the
	 * given name in the configuration cache.
	 * 
	 * @param regionName
	 * @param configurationValue
	 */
	@POST
	@Path("{region}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putCacheEntry(@PathParam("region") String regionName, ConfigurationValue configurationValue) {
		ConfigurationKey configurationKey = new ConfigurationKey(configurationValue.getName());

		this.configurationCache.put(regionName, configurationKey, configurationValue);

		// Enable Subscription to publish.
//		this.emitNotification(PutNotification.<ConfigurationKey, ConfigurationValue>builder() //
//				.key(configurationKey)//
//				.value(configurationValue) //
//				.regionName(regionName) //
//				.value(configurationValue) //
//				.build());
	}

	@PostConstruct
	public void startup() {

		/* Initialize CacheManager for ConfigurationCache. */
		this.configurationCacheManager.manageCache(this.configurationCache);

		/* Subscribe to subscription. */
		this.subscriber.subscribe(this.subscription, new NotificationListener<Notification<ConfigurationKey>>() {

			@Override
			public void onNotification(Notification<Notification<ConfigurationKey>> notification) {
				// TODO
//				for (String updateTopic : configuration.getUpdateTopics()) {
//					notificationProducer.send(new ProducerRecord<Long, Notification>(updateTopic, notification));
//				}

			}
		});
	}

	@PreDestroy
	public void shutdown() {
		/* Shutdown CacheManager. */
		this.configurationCacheManager.releaseCache();
	}

}
