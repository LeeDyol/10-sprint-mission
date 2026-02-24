# 1. 빌드 단계
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
# ./gradlew 대신 sh gradlew를 써서 스크립트 실행임을 못 박습니다!
RUN sh gradlew bootJar -x test

# 2. 실행 단계
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]