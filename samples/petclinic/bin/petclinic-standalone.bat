@echo off
echo.
echo Petclinic RCP [Standalone release]
echo.

if NOT exist lib\acegi-security.jar goto missing_jars
if NOT exist lib\aopalliance.jar goto missing_jars
if NOT exist lib\burlap.jar goto missing_jars
if NOT exist lib\commons-logging.jar goto missing_jars
if NOT exist lib\concurrent.jar goto missing_jars
if NOT exist lib\forms.jar goto missing_jars
if NOT exist lib\hessian.jar goto missing_jars
if NOT exist lib\hsqldb.jar goto missing_jars
if NOT exist lib\jh.jar goto missing_jars
if NOT exist lib\looks.jar goto missing_jars
if NOT exist lib\spring.jar goto missing_jars
if NOT exist lib\spring-sandbox.jar goto missing_jars
if NOT exist lib\spring-petclinic.jar goto missing_jars
if NOT exist lib\spring-richclient.jar goto missing_jars
if NOT exist lib\spring-richclient-resources.jar goto missing_jars

REM The JAR has in its MANIFEST.MF all the above classpath JARs
java -jar petclinic-standalone.jar
goto end

:missing_jars
echo ERROR: Required JARs not found in current_working_directory/lib.

:end
