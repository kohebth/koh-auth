FROM openjdk:11-jdk-slim-buster

COPY auth/build/distributions/auth-1.0-SNAPSHOT.tar /opt/dist.tar
WORKDIR /
RUN tar -xf /opt/dist.tar
RUN mv auth-1.0-SNAPSHOT/bin/* /bin
RUN mv auth-1.0-SNAPSHOT/lib/* /lib
RUN rm /opt/dist.tar
ENV JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8081"

#ENTRYPOINT [ "bash", "-c", "tar -xvf /opt/dist.tar --strip-components=1" ]
ENTRYPOINT [ "auth", "koh.service.auth.App" ]
