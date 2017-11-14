#!/bin/bash

cd /usr/lib/kafka/bin
./zookeeper-server-start.sh ../config/zookeeper.properties &

./kafka-server-start.sh ../config/server.properties
