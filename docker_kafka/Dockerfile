FROM openjdk 

# Add Apache Kafka 1.0.0 to image
ADD kafka_2.11-1.0.0 /usr/lib/kafka

#Prepare directory structure for Zookeeper
RUN mkdir -p /var/data/zookeeper
RUN chmod a+rw /var/data/zookeeper

CMD ["/usr/lib/kafka/bin/standalone-start.sh"]

EXPOSE 4141
