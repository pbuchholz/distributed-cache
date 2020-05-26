# Distributed Cache
Defines a distributed caching system which uses Apache Kafka as synchronization mechanismn between several cache instances. The individual cache instances are build using Java EE 8 and are deployed as WAR files.

## Architectural patterns involved

### EventSourcing
TODO description of the pattern
TODO Doing EventSourcing with Kafka Streams.

### EventDrivenArchitecture (EDA)

## Apache Kafka APIs used

### Producer API
TODO

### Consumer API
TODO

## APIs provided
The distributed cache provides several HTTP based (REST) APIs:

* **GET context-root/configuration/{region-name}/**: Returns the available CacheEntries from the cache region designated as path variable.
* **POST context-root/configuration/{region-name}/**: Use application/json as content-type and transfer the JSON representation of the CacheEntry which should be put into the cache instance in case. Created a new CacheEntry leads to emitting a Notification about the new CacheEntry to the other distributed cache instances.

An example of such a request send against localhost could be: <code>curl -d '{"cache-key":"1", "value":"cache-value"}' -H 'content-type:application/json'  http://localhost:8080/distributedcache/configuration/root.configuration/</code>

* **GET context-root/application-configuration/**: Returns the configuration the application is currently running with as JSON.

## Configuration

### application.properties
The application.properties is used to configure an individual cache instance. 

It has the following properties:
* **kafka.bootstrap.servers:** Used to configure the bootstrap server of Kafka.
* **kafka.cache.in.topic:** Used to configure the topic to listen for incomming updates. For example "cache-updates-${self-instance-identifier}". Also see (1).
* **kafka.cache.out.topic:** Used to configure the topic to send updates to. For example "cache-updates-${pair-instance-identifier}". Also see (1).
* **kafka.cache.fail.topic:** Name of topic to send failures to.
* **cache.invalidation.timer.period:** Miliseconds after which the invalidation timer start the invalidation procedure for cache entries.

**(1):** The idea is that each cache instance is finding a pair instance and collaborates with the respective topics to propagate updates trough the system automatically. This is to be implemented. For the pairing algorithm for example a graph could be used.

All the properties are injected using CDI producers.

## Infrastructure

### Docker
To ease running the services on Apache Kafka a rundimentary Dockerfile and startup script to run Apache Kafka and Apache Zookeeper in a single Docker container is provided in /docker_kafka. The standalone_start.sh shell script has to be copied to the bin folder of an Apache Kafka installation. The Apache Kafka installation must be downloaded manually and will be copied into the image via docker build.

Run docker **build -t docker_kafka .** in the folder of the Dockerfile and also include the Apache Kafka installation in this folder.

