@echo off

set OPTIONS=-Djava.util.logging.config.file=enrollmgr.logging.properties -Djava.library.path=. -DENROLL_TOOL_HOME=. -Daxis2.xml=./axis2.xml


..\..\java\jre\bin\java.exe  %OPTIONS% -jar enrollment.jar %*

