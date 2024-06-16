#!/usr/bin/env bash

# AWS CodeDeploy 애플리케이션 이름
APPLICATION_NAME="ootdzip-CD"

# 현재 배포 그룹 이름을 가져옵니다.
DEPLOYMENT_GROUP_NAME=$(aws deploy get-deployment-group --application-name $APPLICATION_NAME --deployment-group-name $DEPLOYMENT_GROUP_NAME --query "deploymentGroupInfo.deploymentGroupName" --output text)

# 배포 그룹에 따라 대상 디렉토리를 설정합니다.
if [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-cd-group" ]; then
    PROJECT_ROOT="/home/ubuntu/main"
    JAR_FILE_NAME = "ootdzip-prod.jar"
elif [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-dev-group" ]; then
    PROJECT_ROOT="/home/ubuntu/develop"
    JAR_FILE_NAME = "ootdzip-dev.jar"
else
    echo "Unknown deployment group: $DEPLOYMENT_GROUP_NAME"
    exit 1
fi

JAR_FILE="$PROJECT_ROOT/build/libs/$JAR_FILE_NAME" #build.gradle에서 설정한 파일명으로 변경

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -z $CURRENT_PID ]; then
  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행중인 $CURRENT_PID 애플리케이션 종료 " >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi
