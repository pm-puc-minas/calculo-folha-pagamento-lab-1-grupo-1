# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests -Dmaven.test.skip=true package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Opcional: definir flags extras em tempo de execução
ENV JAVA_OPTS=""

# JAR gerado pelo build
COPY --from=build /app/target/folha-pagamento-0.0.1-SNAPSHOT.jar /app/app.jar

# Persistência do H2 (./data dentro do container)
VOLUME ["/app/data"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
