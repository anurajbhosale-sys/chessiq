# ---- STAGE 1: the workshop (JDK 21 + Maven, builds the jar) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first and pre-download dependencies. CONCEPT: Docker caches
# each layer; if pom.xml hasn't changed, it reuses the downloaded deps
# instead of re-fetching them every build. Big speedup.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy source and build the jar.
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- STAGE 2: the showroom (JRE only, runs the jar) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy ONLY the finished jar out of the workshop stage. Everything else
# from stage 1 — JDK, Maven, source, .m2 cache — is left behind.
COPY --from=build /app/target/chessiq-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]