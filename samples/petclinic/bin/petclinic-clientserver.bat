@echo off
echo.
echo Petclinic RCP [Client-Server release]
echo.
echo REQUIRES THE SERVER TO BE CURRENTLY RUNNING!
echo.

REM The JAR has in its MANIFEST.MF all the above classpath JARs
java -jar petclinic-clientserver.jar

:end
