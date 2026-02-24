# 1. 빌드 스테이지
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# 2. 실행 스테이지
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# 빌드된 jar 파일만 쏙 뽑아오기
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]