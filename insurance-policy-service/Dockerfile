FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/insurance-policy-service-1.0-SNAPSHOT.jar app.jar
COPY wait_for_mysql.sh wait_for_mysql.sh
RUN chmod +x wait_for_mysql.sh

COPY start.sh start.sh
RUN chmod +x start.sh

COPY config.yml config.yml

CMD ["./start.sh"]

EXPOSE 8080
