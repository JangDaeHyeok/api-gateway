FROM sktellecom/centos7:jdk-11-redis
VOLUME /tmp
EXPOSE 8080
ADD ./build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-jar","/app.jar"]