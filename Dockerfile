FROM tomcat:7
MAINTAINER funkydorian

ENV CATALINA_HOME /usr/local/tomcat

COPY services/target/digout.war $CATALINA_HOME/webapps

EXPOSE 8080
