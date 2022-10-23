#!/bin/sh

OPTIONS="-Djava.library.path=. -Djava.util.logging.config.file=enrollmgr.logging.properties -cp ../common/lib/commons-logging-1.2.jar:../common/lib/activation-1.1.jar:app-framework.jar:../common/lib/commons-discovery-0.2.jar:enrollment-service.jar:./client-security-config.jar:enrollment.jar:../common/lib/jargs.jar:../common/lib/dom4j-2.1.3.jar:common-framework.jar:../common/lib/server-shared-types.jar:server-shared-services.jar:../common/lib/axis.jar:../common/lib/jargs.jar:../common/lib/jaxrpc.jar:../common/lib/saaj.jar:server-tools-common.jar:../common/lib/wsdl4j-1.5.1.jar:../common/lib/mail-1.4.jar:server-shared-enrollment.jar -DENROLL_TOOL_HOME=. -Daxis2.xml=./axis2.xml"

#JAVA="$JAVA_HOME/bin/java"

../../java/jre/bin/java $OPTIONS com.bluejungle.destiny.tools.enrollment.PropertyMgr "$@"
