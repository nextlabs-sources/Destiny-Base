#!/bin/bash

if [ -z "$JRE_HOME" ]; then
    echo "Please set JRE_HOME to the location of a Java 8 Runtime"
    exit 1
fi

$JRE_HOME/java -classpath "`dirname "$0"`/decryptj/*" com.bluejungle.destiny.agent.tools.DecryptBundle -e `dirname "$0"`
