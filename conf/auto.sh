#!/bin/bash


for i in $(seq 1 1 100000)
do
	curl -X GET "http://localhost:9000/event/message/a/$RANDOM"
	curl -X GET "http://localhost:9000/event/message/b/$RANDOM"
	curl -X GET "http://localhost:9000/event/message/c/$RANDOM"
	curl -X GET "http://localhost:9000/event/message/d/$RANDOM"
done
