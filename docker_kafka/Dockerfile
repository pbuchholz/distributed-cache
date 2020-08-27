FROM debian:stable 
RUN apt-get update
RUN apt-get upgrade
RUN apt-get -y install wget

# Add Apache Kafka 2.12-2.6.0 to the image
RUN wget http://apache.mirror.iphh.net/kafka/2.6.0/kafka_2.12-2.6.0.tgz
RUN tar -zxvf ./kafka_2.12-2.6.0.tgz
RUN mv ./kafka_2.12-2.6.0 /usr/lib/kafka

#Prepare directory structure for Zookeeper
RUN mkdir -p /var/data/zookeeper
RUN chmod a+rw /var/data/zookeeper

ADD ./standalone-start.sh /usr/lib/kafka/bin

CMD ["/usr/lib/kafka/bin/standalone-start.sh"]

EXPOSE 4141
