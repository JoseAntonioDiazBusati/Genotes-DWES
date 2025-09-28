#!/usr/bin/env sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Bootstrapping Gradle Wrapper (requires local 'gradle' once)..."
  if command -v gradle >/dev/null 2>&1; then
    (cd "$APP_HOME" && gradle wrapper --gradle-version 8.9) || exit 1
  else
    echo "Install Gradle once and run: gradle wrapper --gradle-version 8.9"; exit 1
  fi
fi
JAVA_EXE="${JAVA_HOME:+$JAVA_HOME/bin/}java"
exec "$JAVA_EXE" -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
