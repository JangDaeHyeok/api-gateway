FROM sktellecom/centos7:jdk-11-redis
VOLUME /tmp
EXPOSE 8090
ADD ./build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
CMD ["redis-server"]
ENTRYPOINT ["java","-jar","/app.jar"]