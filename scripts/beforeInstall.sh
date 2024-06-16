#!/bin/bash

# AWS CodeDeploy 애플리케이션 이름
APPLICATION_NAME="ootdzip-CD"

# 현재 배포 그룹 이름을 가져옵니다.
DEPLOYMENT_GROUP_NAME=$(aws deploy get-deployment-group --region ap-northeast-2 --application-name $APPLICATION_NAME --deployment-group-name $DEPLOYMENT_GROUP_NAME --query "deploymentGroupInfo.deploymentGroupName" --output text)

# 배포 그룹에 따라 대상 디렉토리를 설정합니다.
if [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-cd-group" ]; then
    DESTINATION_DIR="/home/ubuntu/main"
elif [ "$DEPLOYMENT_GROUP_NAME" == "ootdzip-dev-group" ]; then
    DESTINATION_DIR="/home/ubuntu/develop"
else
    echo "Unknown deployment group: $DEPLOYMENT_GROUP_NAME"
    exit 1
fi

echo "Moving files to $DESTINATION_DIR"

# 해당 디렉토리가 없으면 생성
mkdir -p $DESTINATION_DIR

# /tmp/deployment 디렉토리의 파일을 대상 디렉토리로 이동
mv /home/ubuntu/deploy/* $DESTINATION_DIR/
