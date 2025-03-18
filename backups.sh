#!/bin/bash

# Da cambiare in funzione dell'installazione corrente
SERVICE_DIR=/home/anac/public-sites-service

echo "ESECUZIONE delle operazioni di backup databases di public-sites-service"

date=`date +%Y%m%d-%H%M`


BACKUP_DIR=${SERVICE_DIR}/backups

find $BACKUP_DIR -mtime +30 | xargs -r rm
cd $SERVICE_DIR

docker compose -f $SERVICE_DIR/docker-compose.yml exec -T postgres pg_dump -U cnr-anac public-company-sites > $BACKUP_DIR/public-sites-service-$date.sql

gzip -9 $BACKUP_DIR/public-sites-service-$date.sql
