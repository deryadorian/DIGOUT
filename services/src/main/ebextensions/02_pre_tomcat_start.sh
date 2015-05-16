#!/bin/sh

ln -s /var/lib/tomcat7/webapps/ROOT /var/lib/tomcat7/webapps/digout

cp /tmp/digout.xml /etc/tomcat7/Catalina/localhost/

/sbin/service tomcat7 restart
