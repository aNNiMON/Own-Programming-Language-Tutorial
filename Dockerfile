FROM gradle:8.13.0-jdk21-alpine AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle /home/gradle/java-code/
WORKDIR /home/gradle/java-code
RUN GRADLE_OPTS="-Xmx256m" gradle build --build-cache --stacktrace -i --no-daemon

FROM gradle:8.13.0-jdk21-alpine AS builder
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/java-code
WORKDIR /usr/src/java-code
RUN GRADLE_OPTS="-Xmx256m" gradle shadowJar --build-cache --stacktrace --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/ownlang
ENV PATH /opt/ownlang:$PATH
COPY --from=builder /usr/src/java-code/ownlang-desktop/build/libs/*.jar libs/OwnLang.jar
COPY --from=builder /usr/src/java-code/modules/jdbc/build/libs/*.jar modules/
COPY --from=builder /usr/src/java-code/modules/server/build/libs/*.jar modules/
COPY --from=builder /usr/src/java-code/modules/socket/build/libs/*.jar modules/
COPY dist/own .
COPY dist/ownlang .
RUN chmod +x own ownlang
ENTRYPOINT ["ownlang"]

