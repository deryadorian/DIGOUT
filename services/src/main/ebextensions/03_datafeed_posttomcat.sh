#!/bin/sh

/bin/sleep 60

RDS_HOSTNAME="$(awk -F= '/RDS_HOSTNAME/{print $2}' /etc/environment)"
RDS_USERNAME="$(awk -F= '/RDS_USERNAME/{print $2}' /etc/environment)"
RDS_PASSWORD="$(awk -F= '/RDS_PASSWORD/{print $2}' /etc/environment)"

/usr/bin/mysql -u $RDS_USERNAME -p$RDS_PASSWORD -h $RDS_HOSTNAME digout < /opt/elasticbeanstalk/hooks/appdeploy/post/GuestUser

/usr/bin/mysql -u $RDS_USERNAME -p$RDS_PASSWORD -h $RDS_HOSTNAME digout < /opt/elasticbeanstalk/hooks/appdeploy/post/AppVersion

/usr/bin/mysql -u $RDS_USERNAME -p$RDS_PASSWORD -h $RDS_HOSTNAME digout < /opt/elasticbeanstalk/hooks/appdeploy/post/VPOS
