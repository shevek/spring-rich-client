@echo off
echo.
echo Petclinic RCP [Standalone release]
echo.

REM The JAR has in its MANIFEST.MF all the above classpath JARs
java -jar petclinic-standalone.jar
goto end

:end
