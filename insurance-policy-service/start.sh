#!/bin/sh
./wait_for_mysql.sh
java -jar app.jar db migrate config.yml
exec java -jar app.jar server config.yml

