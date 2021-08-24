FROM openjdk:16-jdk-alpine
WORKDIR ..
RUN pwd
COPY target/probation-0.0.1-SNAPSHOT.jar probation.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "probation.jar", "--spring.profiles.active=${ADDITIONAL_APP_PROFILES:local}"]
