#!/bin/bash

ROOT=$CATALINA_HOME

if [ -z "$ROOT" ]; then
    ROOT=$JBOSS_HOME
fi

if [ -z "$JRE_HOME" ]; then
    echo "Please set JRE_HOME to the location of Java Runtime"
    exit 1
fi

$JRE_HOME/bin/java -classpath "$ROOT/nextlabs/dpc/decryptj/*" com.bluejungle.destiny.agent.tools.DecryptBundle 
