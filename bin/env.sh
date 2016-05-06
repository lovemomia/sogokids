#!/bin/bash

JAVA_OPTS="-Xms2G -Xmx2G -Xmn512M \
	-XX:PermSize=64M -XX:MaxPermSize=64M \
	-XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 \
	-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+DisableExplicitGC \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888"
