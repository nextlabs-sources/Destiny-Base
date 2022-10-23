@echo off
rem $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/pf/etc/importLocations.bat#1 $

..\java\jre\bin\java.exe -noverify -Djavax.net.ssl.trustStore=../server/certificates/cacerts.jks -jar locationimporter/location-importer.jar %*
