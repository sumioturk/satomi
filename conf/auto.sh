#!/bin/bash


for i in $(seq 1 1 100000)
do
	curl -X GET "http://localhost:9000/event/message/517384cd3004b86c5af6ee8e/$RANDOM"
done
