#!/bin/bash

OOTD_PID=$(ps -ef|grep java|grep ootdzip|awk '{print $2}')

if [ -z "$OOTD_PID" ];
then
	echo "OOTDZIP Server is not running"
else
	sudo kill -9 $OOTD_PID
	echo "OOTDZIP Server Stopped"
fi
