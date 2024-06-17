#!/usr/bin/env bash

# AWS CodeDeploy 애플리케이션 이름
APPLICATION_NAME="ootdzip-CD"

# 현재 배포 그룹 이름을 가져옵니다.
DEPLOYMENT_GROUP_NAME=$(aws deploy get-deployment-group --region ap-northeast-2 --application-name $APPLICATION_NAME --deployment-group-name $DEPLOYMENT_GROUP_NAME --query "deploymentGroupInfo.deploymentGroupName" --output text)

PROJECT_ROOT="/home/ubuntu/server" #코드가 주입되는 경로
JAR_FILE="$PROJECT_ROOT/build/libs/ootdzip-0.0.1-SNAPSHOT.jar" #build.gradle에서 설정한 파일명으로 변경

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

echo "$TIME_NOW > $JAR_FILE 파일 실행 DEPLOYMENT_GROUP_NAME: $DEPLOYMENT_GROUP_NAME" >> $DEPLOY_LOG


if [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-cd-group" ]; then
    nohup java -jar $JAR_COMMAND --spring.profiles.active=prod > $APP_LOG 2> $ERROR_LOG &
elif [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-cd-dev" ]; then
    nohup java -jar $JAR_COMMAND --spring.profiles.active=dev > $APP_LOG 2> $ERROR_LOG &
else
    echo "Unknown deployment group: $DEPLOYMENT_GROUP_NAME"
    exit 1
fi

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG