FROM eclipse-temurin:21-jre-alpine

RUN mkdir /app

COPY registro-1.0.0-SNAPSHOT.jar /app/registro.jar
COPY registro.properties /app/registro.properties

EXPOSE 8761

HEALTHCHECK CMD /usr/bin/wget -O - http://localhost:8761 || exit 1

CMD ["java", "-jar", "/app/registro.jar"]
