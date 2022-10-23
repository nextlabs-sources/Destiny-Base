@echo off

IF DEFINED JRE_HOME (
  set JAVADIR="%JRE_HOME%"
) ELSE (
  IF DEFINED JAVA_HOME (
    set JAVADIR="%JAVA_HOME%"
  ) ELSE (
    echo "Please set JRE_HOME to the location of Java Runtime"
    exit /B
  )
)

%JAVADIR%\bin\java -classpath %~dp0/decryptj/* com.bluejungle.destiny.agent.tools.DecryptBundle -e $~dp0
