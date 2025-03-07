#
# Clojure Application Dockerfile
#
# The JRE_TAG can be passed in at build time.
#
ARG JRE_TAG=23-jre
#
FROM eclipse-temurin:${JRE_TAG} AS stage1

#
# Add in a few debugging tools that are *very* useful in determining if the container can ping/access a remote port.
#
RUN apt-get update \
    && apt-get upgrade -qq -y \
    && apt-get install -qq -y \
        curl \
        dnsutils \
        iputils-ping \
        less \
        netcat-openbsd \
        procps \
        telnet \
        unzip \
        vim-tiny \
        wget

#
# Use British English UTF-8 Locale (and switch off the American locale)
#
RUN sed -i '/en_GB.UTF-8/s/^# //g' /etc/locale.gen && sed -i '/en_US/s/^/#/' /etc/locale.gen && locale-gen

#
# Clean everything up
#
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

#
# Jump to Stage 2
#
FROM stage1 AS stage2

#
# Add in the Prometheus JMX Exporter for remote monitoring/diagnostics
#
ARG JMX_EXPORTER_VERSION=1.1.0
#
RUN wget --quiet https://github.com/prometheus/jmx_exporter/releases/download/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar -O /tmp/jmx_prometheus_javaagent.jar

#
# Jump to Stage 3
#
FROM stage2 AS stage3

#
# HTTP
#
EXPOSE 8080

#
# JMXMP (see src/**/jmx.clj)
#
EXPOSE 5555

#
# JMX (not JMXMP! - that is started by the server - see src/**/jmx.clj)
#
EXPOSE 9010

#
# JMX EXPORTER
#
EXPOSE 9080

#
# Set system locale
#
ENV LANG=en_GB.UTF-8
ENV LC_ALL=en_GB.UTF-8

#
# Options that are passed to the JVM when it is launched
#
ENV JVM_OPTS=""

#
# JMX port (not JMXMP! - that is started by the server - see src/**/jmx.clj)
#
ENV JMX_PORT=9010

#
# JMX EXPORTER port
#
ENV JMX_EXPORTER_PORT=9080

#
# Options that are passed to the APP when it is launched
#
ENV RUN_OPTS=""

# ENVIRONMENT (can be overridden during the container build)
ARG ENVIRONMENT="local"

#
# Export the Environment so that the launch script can pick it up
#
ENV ENVIRONMENT=$ENVIRONMENT

#
# Application name (must be overridden during the container build)
#
ARG APPLICATION

#
# Export the Project name so that the launch script can pick it up
#
ENV APPLICATION=$APPLICATION

#
# JAR name (as named during the uberjar task - can be overridden during the container build)
#
ARG APPLICATION_JAR="app.jar"

#
# Export the name so that the launch script can pick it up
#
ENV APPLICATION_JAR=$APPLICATION_JAR

#
# Application Version (usually just `git rev-parse --short=8 HEAD`)
#
ARG VERSION

#
# Export the Version so that the launch script can pick it up
#
ENV VERSION=$VERSION

#
# Jump to Stage 4
#
FROM stage3 AS stage4

#
# "Contain" the exec of launch.sh to user "clojure" This means that *if* someone
# was to gain access to the running container, and *if* the user was able to
# break out of the container, they would escape as the user "clojure" and thus on
# the host, they would have to perform *another* exploit to gain access as
# root.
#
RUN groupadd -r clojure && useradd -m --no-log-init -rg clojure clojure

#
# Change to the user's home directory
#
WORKDIR /home/clojure

#
# From this point onwards, become the clojure user for (mostly) all operations.
#
USER clojure

#
# Copy the launch script into place
#
COPY --chown=clojure:clojure scripts/docker/launch.sh launch.sh

#
# Copy the Prometheus JMX Exporter config into place
#
COPY --chown=clojure:clojure scripts/docker/jmx-exporter-config.yml jmx-exporter-config.yml

#
# Copy the jstatd permissions policy into place
#
COPY --chown=clojure:clojure scripts/docker/jstatd.policy jstatd.policy

#
# Jump to Stage 5
#
FROM stage4 AS stage5

#
# Copy the Uberjar into place
#
COPY --chown=clojure:clojure target/$APPLICATION_JAR $APPLICATION_JAR

#
# And awaayyy we go!
#
CMD ["./launch.sh"]
