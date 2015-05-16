#!/bin/sh
/usr/bin/mysql \
    -u $RDS_USERNAME \
    -p$RDS_PASSWORD \
    -h $RDS_HOSTNAME \
    $RDS_DB_NAME \
    -e 'create schema digout'

sed -i 's/RDS_HOSTNAME/'$RDS_HOSTNAME'/g' /etc/digout/digout.properties

echo RDS_HOSTNAME=$RDS_HOSTNAME >> /etc/environment
echo RDS_USERNAME=$RDS_USERNAME >> /etc/environment
echo RDS_PASSWORD=$RDS_PASSWORD >> /etc/environment
