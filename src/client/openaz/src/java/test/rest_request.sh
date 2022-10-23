#!/bin/bash
shopt -s nullglob
export CURRENT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
export CLASSPATH="$CURRENT_DIR:$CURRENT_DIR/../libs/*:$CURRENT_DIR/config"

for f in "$CURRENT_DIR/"*.java
do
  filename=$(basename "$f" | sed 's/\.[^.]*$//')
  echo Compiling Java code: "$filename.java"
  javac -cp "$CURRENT_DIR/../libs/*" "$f"
  echo Executing Java class: $filename
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=info -Dconfig.file=openaz-pep-rest.properties $filename
done

