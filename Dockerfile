FROM alpine:3.9
MAINTAINER Hannes Angst <hannes@angst.email>

#
# It's always good to define this with java.
#
VOLUME /tmp

# default spring profile
ENV SPRING_PROFILE  docker

# default tomcat port
ENV PORT            8080

# Default to UTF-8 file.encoding
ENV LANG            C.UTF-8

ENV JAVA_HOME       /usr/lib/jvm/java-1.8-openjdk/jre
ENV PATH             $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin
ENV HOME            /home/javarun
ENV JAVA_MAIN_CLASS "cloud.angst.k8s.kubestatus.Application"
#
# Not sure about the IDs for group and user.
# However, we need be have an ID higher than 1024 (because of reasons)
#
ARG PGID=9002
ARG PUID=9002


RUN addgroup -g ${PGID} javarun && \
    adduser -D -u ${PUID} -G javarun javarun && \
    mkdir -p /home/javarun &&  \
    mkdir -p /home/javarun/libs && \
    chown -R javarun:javarun /home/javarun && \
    apk update  --no-cache &&  \
    apk upgrade --no-cache && \
    apk add --no-cache curl bash nss openjdk8-jre-base && \
    rm -rf /tmp/* /var/cache/apk/*





# Add dependencies
COPY target/dependency /home/javarun/libs

# Add jar
COPY target/app.jar.original /home/javarun/app.jar


#
# Allow r/w to copied data
#
RUN chown -R javarun:javarun /home/javarun

#
# Use the created space to work at
#
WORKDIR /home/javarun

#
# Be the previously created user
#
USER javarun

#
# The port we want to be called upon.
#
EXPOSE $PORT

# for
#     -Djava.security.egd=file:/dev/./urandom
# see
#     http://ruleoftech.com/2016/avoiding-jvm-delays-caused-by-random-number-generation
#
# Note: Since java 8, the -Xmx is only considered as Heapspace size
#       and not as maximal memory consuption of the application.
#       Added up meta, eden and surviver space, als well as the stack
#       we asume the following:
#       -Xmx 512M means
#       - We will at least consume 512 Mbyte of RAM
#       - In addition, we will end up this 256 MByte of RAM for other "spaces"
#       - We add another 256 Mbyte of RAM for good fortune and buffer.
#
#       => This application needs to be killed if more than 1G of RAM is consumed.
#
# -XX:+UseCGroupMemoryLimitForHeap  - Backport for jdk8 to consider docker environment
# -XX:+UseConcMarkSweepGC           - Do not GC when it is still to late and GC will use precious time
# -Djava.awt.headless=true          - We need to signal headless jre to the jvm.
#
# Note: The past the host name to the application for simpler usage.
#
#
ENTRYPOINT exec java \
 \
 ${JAVA_OPTS} \
 \
 -cp 'libs/*:app.jar' \
 \
 -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -XX:MaxRAMFraction=1 \
 -XX:+UseConcMarkSweepGC \
 \
 -Djava.awt.headless=true \
 -Djava.security.egd=file:/dev/./urandom \
 -Dspring.profiles.active="${SPRING_PROFILE}" \
 -Dfile.encoding=UTF-8 \
 -Dserver.port=${PORT} \
 -DAPP_HOSTNAME="`hostname -f`" \
 ${JAVA_MAIN_CLASS}
