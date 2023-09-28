#!/bin/bash
container_uuid=$(uuidgen | cut -c 1-8)

docker build -t dialogdb .

docker run -d -p 8080:8080 --name "dialog_container_$container_uuid" dialogdb

sleep 5

# open http://localhost:8080

echo "dialog_container_$container_uuid"

docker exec -it "dialog_container_$container_uuid" /bin/bash