#!/bin/sh

../java/jre/bin/java -noverify -Djavax.net.ssl.trustStore=../server/certificates/cacerts.jks -jar locationimporter/location-importer.jar "$@"
