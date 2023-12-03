#!/bin/bash

JAR=ootdzip-0.0.1-SNAPSHOT.jar
cd /home/ubuntu/server/build/libs
nohup java -jar $JAR &
