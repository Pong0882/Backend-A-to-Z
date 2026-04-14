#!/bin/bash

# .env 파일 로드
set -a
source .env
set +a

# prometheus.yml.template → prometheus.yml 생성 (IP 치환)
envsubst < prometheus.yml.template > prometheus.yml

echo "prometheus.yml 생성 완료 (PONG_SERVER_IP: ${PONG_SERVER_IP})"

# 컨테이너 실행
docker compose up -d
