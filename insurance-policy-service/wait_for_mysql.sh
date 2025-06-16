#!/bin/sh
echo "Esperando para o banco ficar pronto..."
sleep 60

echo "Iniciando aplicação..."
exec java -jar app.jar server config.yml

