@ECHO OFF

echo [Setting petclinic build environment]

if "%JAVA_HOME%" == "" set JAVA_HOME=C:/j2sdk1.4.2

set SPRING_HOME=../../../spring

echo    JAVA_HOME=%JAVA_HOME%
echo    SPRING_HOME=%SPRING_HOME%

echo [Launching ant]
%JAVA_HOME%/bin/java -cp %SPRING_HOME%/lib/ant/ant.jar;%SPRING_HOME%/lib/ant/ant-launcher.jar;%SPRING_HOME%/lib/ant/ant-junit.jar;%SPRING_HOME%/lib/junit/junit.jar;%JAVA_HOME%/lib/tools.jar org.apache.tools.ant.Main %1
