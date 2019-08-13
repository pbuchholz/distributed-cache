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
import distributedcache.Cache;
import distributedcache.CacheEntry;
import distributedcache.CacheRegion;
import distributedcache.configuration.ConfigurationCache;
import distributedcache.configuration.ConfigurationKey;
import distributedcache.configuration.ConfigurationValue;
import distributedcache.notification.Notification;
import distributedcache.notification.NotificationListener;
import distributedcache.notification.PutNotification;
import distributedcache.notification.kafka.ProducerRecordFactory;
import distributedcache.notification.kafka.ProducerRecordFactory.PartitionKeys;

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
	private Consumer<PartitionKeys, Notification> notificationConsumer;

	@Inject
	@KeySerializer
	@ValueSerializer("distributedcache.configuration.boundary.NotificationSerializer")
	private Producer<PartitionKeys, Notification> notificationProducer;

	// TODO Doesnt work
	private Event<Notification> notificationEvent;

	@Inject
	@ConfigurationCache
	private Cache<ConfigurationKey, ConfigurationValue> configurationCache;

	@Inject
	private ApplicationConfiguration configuration;

	@Inject
	private ProducerRecordFactory producerRecordFactory;

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

		CacheEntry<ConfigurationKey, ConfigurationValue> cacheEntry = CacheEntry
				.<ConfigurationKey, ConfigurationValue>builder() //
				.key(new ConfigurationKey(configurationValue.getName())) //
				.value(configurationValue) //
				.build();

		this.configurationCache.put(regionName, cacheEntry);
	}

	@PostConstruct
	public void startup() {

		/* Register NotificationListener. */
		this.configurationCache.registerNotificationListener(new NotificationListener() {

			@Override
			public void onNotification(Notification notification) {
				notification.setSource(configuration.getPeerPartition());
				ProducerRecord<PartitionKeys, Notification> producerRecord = producerRecordFactory
						.createProducerRecord(PartitionKeys.Peer, notification);
				notificationProducer.send(producerRecord);
			}
		});

		/* Start ManagedExecutor for Kafka. */
		this.executorService.execute(kafkaWorker);
	}

	@PreDestroy
	public void shutdown() {
		this.kafkaWorker.shutdown();
	}

	private class KafkaWorker implements Runnable {

		private AtomicBoolean shutdown = new AtomicBoolean();

		@Override
		public void run() {
			while (!shutdown.get()) {
				ConsumerRecords<PartitionKeys, Notification> records = notificationConsumer.poll(Duration.ofDays(1L));
				records.forEach((consumerRecord) -> {

					Notification notification = consumerRecord.value();

					LOGGER.info("Notification {} received", notification);

					switch (notification.type()) {
					case PUT:
						PutNotification putNotification = (PutNotification) notification;

						configurationCache.put(putNotification.getRegionName(),
								CacheEntry.<ConfigurationKey, ConfigurationValue>builder() //
										.key((ConfigurationKey) putNotification.getCacheKey()) //
										.value((ConfigurationValue) putNotification.getValue()) //
										.created(System.currentTimeMillis()) //
										.build());

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
