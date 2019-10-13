package distributedcache.configuration.boundary;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import distributedcache.ApplicationConfiguration;
import distributedcache.cache.Cache;
import distributedcache.cache.CacheEntry;
import distributedcache.cache.CacheManager;
import distributedcache.cache.CacheRegion;
import distributedcache.configuration.ConfigurationCache;
import distributedcache.configuration.ConfigurationKey;
import distributedcache.configuration.ConfigurationValue;
import distributedcache.notification.Notification;
import distributedcache.notification.PutNotification;

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
	@Consume
	@ConsumerGroup("cache.notifications")
	@KeyDeserializer
	@ValueDeserializer("distributedcache.configuration.boundary.NotificationDeserializer")
	private Consumer<Long, Notification> notificationConsumer;

	@Inject
	@KeySerializer
	@ValueSerializer("distributedcache.configuration.boundary.NotificationSerializer")
	private Producer<Long, Notification> notificationProducer;

	// TODO Doesnt work
	private Event<Notification> notificationEvent;

	@Inject
	@ConfigurationCache
	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;

	@Inject
	private CacheManager<ConfigurationKey, ConfigurationValue> configurationCacheManager;

	@Inject
	private ApplicationConfiguration configuration;

	@Resource
	private ManagedExecutorService executorService;

	private KafkaWorker kafkaWorker = new KafkaWorker();

	/**
	 * Returns a JSON representation of the currently available
	 * {@link CacheRegion}ØÏ and their associated {@link CacheEntry}s.
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfigurationCache() {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		configurationCache.getCacheRegions() //
				.stream() //
				.map(cr -> {
					JsonObjectBuilder cacheEntriesObjectBuilder = Json.createObjectBuilder();
					cr.cacheEntries().stream() //
							.forEach(ce -> {
								cacheEntriesObjectBuilder.add(ce.key().getConfigurationValueName(),
										Json.createObjectBuilder() //
												.add("name", ce.value().getName()) //
												.add("value", ce.value().getValue()) //
												.add("created", ce.getCreated()) //
												.add("lastAccess", ce.getLastAccess()) //
												.add("validationTimespan", ce.getValidationTimespan()) //
												.add("neverAccessed", ce.neverAccessed()));

							});

					return Json.createObjectBuilder() //
							.add("cacheRegion", Json.createObjectBuilder() //
									.add("name", cr.getName()) //
									.addAll(cacheEntriesObjectBuilder));
				}).forEach(jsonObjectBuilder::addAll);
		return jsonObjectBuilder.build().toString();
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

		this.emitNotification(PutNotification.<ConfigurationKey, ConfigurationValue>builder() //
				.key(configurationKey)//
				.value(configurationValue) //
				.regionName(regionName) //
				.value(configurationValue) //
				.build());
	}

	/**
	 * Emits the passed in {@link Notification} to the configured update topics.
	 * 
	 * @param notification
	 */
	private void emitNotification(Notification notification) {
		for (String updateTopic : configuration.getUpdateTopics()) {
			notificationProducer.send(new ProducerRecord<Long, Notification>(updateTopic, notification));
		}
	}

	@PostConstruct
	public void startup() {

		/* Initialize CacheManager for ConfigurationCache. */
		this.configurationCacheManager.manageCache(this.configurationCache);

		/* Subscribe to registered Topic. */
		this.notificationConsumer.subscribe(Collections.singletonList(configuration.getEmitTopic()));

		/* Start ManagedExecutor for Kafka. */
		this.executorService.execute(kafkaWorker);
	}

	@PreDestroy
	public void shutdown() {
		/* Shutdown CacheManager. */
		this.configurationCacheManager.releaseCache();

		/* Shutdown KafkaWorker. */
		this.kafkaWorker.shutdown();
	}

	private class KafkaWorker implements Runnable {

		private AtomicBoolean shutdown = new AtomicBoolean();

		@Override
		public void run() {
			while (!shutdown.get()) {
				ConsumerRecords<Long, Notification> records = notificationConsumer.poll(Duration.ofDays(1L));
				records.forEach((consumerRecord) -> {

					Notification notification = consumerRecord.value();

					LOGGER.info("Notification {} received", notification);

					switch (notification.type()) {
					case PUT:
						PutNotification<ConfigurationKey, ConfigurationValue> putNotification = (PutNotification<ConfigurationKey, ConfigurationValue>) notification;

						configurationCache.put(putNotification.getRegionName(), putNotification.getKey(),
								putNotification.getValue());

						/* Fire PutNotification. */
						// notificationEvent.select(new TypeLiteral<PutNotification>() {
						// private static final long serialVersionUID = 5825137225915787290L;
						// }).fire((PutNotification) notification);
					}

					notificationConsumer.commitAsync(); // TODO Check Where to we have to commit the
														// receival of a message.
				});
			}

		}

		public void shutdown() {
			this.shutdown.compareAndSet(false, true);
		}

	}

}
