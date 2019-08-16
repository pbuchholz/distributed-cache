# Distributed Cache
Defines a distributed caching system which uses Apache Kafka as synchronization mechanismn between several cache instances. The individual cache instances are build using Java EE 8 and are deployed as WAR files.


## EventSourcing
TODO description of the pattern
TODO Doing EventSourcing with Kafka Streams.

## Producing with the Producer API
TODO

## Consuming with the Consumer API
TODO

## Docker
To ease running the services on Apache Kafka a rundimentary Dockerfile and startup script to run Apache Kafka and Apache Zookeeper in a single Docker container is provided in /docker_kafka. The standalone_start.sh shell script has to be copied to the bin folder of an Apache Kafka installation. The Apache Kafka installation must be downloaded manually and will be copied into the image via docker build.

Run docker **build -t docker_kafka .** in the folder of the Dockerfile and also include the Apache Kafka installation in this folder.

