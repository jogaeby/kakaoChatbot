#!/bin/bash

# git pull 명령어 실행
git pull

# git pull 실행 결과 확인
if [ $? -ne 0 ]; then
    echo "Git pull 실패!"
    exit 1
fi

# ./gradlew bootJar 명령어 실행
./gradlew bootJar

# Gradle 빌드 실행 결과 확인
if [ $? -ne 0 ]; then
    echo "Gradle 빌드 실패!"
    exit 1
fi

echo "Gradle 빌드 성공!"

# docker-compose up --build -d 명령어 실행
docker compose up --build -d

# Docker Compose 실행 결과 확인
if [ $? -ne 0 ]; then
    echo "Docker Compose 실행 실패!"
    exit 1
fi

echo "Docker Compose 실행 성공!"

# docker image prune 명령어 실행 (불필요한 이미지 제거)
docker image prune -f
