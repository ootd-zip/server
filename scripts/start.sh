#!/bin/bash

chmod 755 /home/ubuntu/server/build/libs/ootdzip-0.0.1-SNAPSHOT.jar
nohup java -jar /home/ubuntu/server/build/libs/ootdzip-0.0.1-SNAPSHOT.jar &
