#!/usr/bin/env bash

set -eu

#
# A helpful message for debugging the startup...
#
echo "This script is executing in the $ENVIRONMENT environment. The version of this application is $VERSION."

#
# Use "exec" to run java -jar as PID 1
#
# also, reduce start up time by pointing to non blocking random number generation
#
exec java \
    -Dcom.sun.management.jmxremote.local.only=false \
    -Dcom.sun.management.jmxremote.port=$JMX_PORT \
    -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT \
    -Dcom.sun.management.jmxremote.host=0.0.0.0 \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Djava.net.preferIPv4Stack=true \
    -Djava.security.egd=file:/dev/./urandom \
    -Djava.rmi.server.hostname=0.0.0.0 \
    -Djna.library.path=/usr/lib/x86_64-linux-gnu \
    -XX:+UseZGC \
    -XX:TieredStopAtLevel=1 \
    -javaagent:/tmp/jmx_prometheus_javaagent.jar=$JMX_EXPORTER_PORT:jmx-exporter-config.yml \
    $JVM_OPTS \
    -jar $APPLICATION_JAR \
    $RUN_OPTS \
    $@
