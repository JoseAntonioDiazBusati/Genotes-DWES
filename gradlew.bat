@ECHO OFF
SETLOCAL
SET APP_HOME=%~dp0
SET WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Bootstrapping Gradle Wrapper (requires local 'gradle' once)...
  WHERE gradle >NUL 2>&1 || (ECHO Please install Gradle and run: gradle wrapper --gradle-version 8.9 & EXIT /B 1)
  PUSHD "%APP_HOME%"
  gradle wrapper --gradle-version 8.9 || (POPD & EXIT /B 1)
  POPD
)
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" SET JAVA_EXE=java
"%JAVA_EXE%" -Dorg.gradle.appname=gradlew -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
ENDLOCAL
