FROM 10.3.98.241/library/java:8-alpine
MAINTAINER fangkun "fangkun@huntkey.net"
ENV TIME_ZONE Asia/Shanghai
RUN apk add --no-cache tzdata && echo "${TIME_ZONE}" > /etc/timezone && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime
VOLUME /tmp
ADD biz-monitor-provider-1.0-SNAPSHOT.jar app.jar
EXPOSE 10042
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.cloud.config.profile=debug"]