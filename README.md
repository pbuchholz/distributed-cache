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

* GET context-root/{region-name}/: Returns the available CacheEntries from the cache region designated as path variable.
* **POST context-root/{region-name}/**: Use application/json as content-type and transfer the JSON representation of the CacheEntry which should be put into the cache instance in case. Created a new CacheEntry leads to emitting a Notification about the new CacheEntry to the other distributed cache instances.

## Configuration

### application.properties
TODO

## Infrastructure

### Docker
To ease running the services on Apache Kafka a rundimentary Dockerfile and startup script to run Apache Kafka and Apache Zookeeper in a single Docker container is provided in /docker_kafka. The standalone_start.sh shell script has to be copied to the bin folder of an Apache Kafka installation. The Apache Kafka installation must be downloaded manually and will be copied into the image via docker build.

Run docker **build -t docker_kafka .** in the folder of the Dockerfile and also include the Apache Kafka installation in this folder.

