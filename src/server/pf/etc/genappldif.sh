#!/bin/sh

../java/jre/bin/java -noverify -Djava.library.path=common/lib -jar genappldif/genappldif.jar "$@"
