@ECHO OFF

echo [Setting spring-richclient build environment]

if "%JAVA_HOME%" == "" set JAVA_HOME=C:/j2sdk1.4.2

if "%SPRING_RCP_HOME%" == "" set SPRING_RCP_HOME=./

echo    JAVA_HOME=%JAVA_HOME%
echo    SPRING_RCP_HOME=%SPRING_RCP_HOME%

echo [Launching ant]
%JAVA_HOME%/bin/java -cp %SPRING_RCP_HOME%/lib/ant/ant.jar;%SPRING_RCP_HOME%/lib/ant/ant-launcher.jar;%SPRING_RCP_HOME%/lib/ant/ant-junit.jar;%SPRING_RCP_HOME%/lib/junit/junit.jar;%JAVA_HOME%/lib/tools.jar org.apache.tools.ant.Main %1
