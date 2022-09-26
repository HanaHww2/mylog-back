# JDK11 이미지 사용
FROM openjdk:11-jdk

# JAR_FILE 변수에 값을 저장, TODO 버전 정보에 따라 변경 수행
ARG VERSION=0.0.1
ARG JAR_FILE=./build/libs/myLog-${VERSION}-SNAPSHOT.jar

# 변수에 저장된 것을 컨테이너 실행시 이름을 app.jar파일로 변경하여 컨테이너에 저장
COPY ${JAR_FILE} app.jar
#COPY ./config /config

# PROFILE이라는 이름의 argument를 받을 수 있도록 설정
ARG PROFILE=prod
# argument로 받은 ENVIRONMENT 값을 SPRING_PROFILES_ACTIVE에 적용
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# 빌드된 이미지가 run될 때 실행할 명령어, 아래와 같이 외부 주입하는 경우 사용하는 설정 파일을 모두 세팅해주어야 한다.(초기화되기 때문)
ENTRYPOINT ["java","-jar", "-Dspring.config.location=/config/application-auth.yml,classpath:/application.yml" ,"app.jar"]
#  ./build/libs/myLog-${VERSION}-SNAPSHOT.jar