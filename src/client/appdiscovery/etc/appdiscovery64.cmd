@echo off

java -Djava.library.path=. -cp appdiscovery.jar;agent-ipc.jar;common-framework.jar;ddif-tools.jar;org.eclipse.core.runtime_3.4.0.v20080512.jar;org.eclipse.core.commands_3.4.0.I20080509-2000.jar;org.eclipse.osgi_3.4.3.R34x_v20081215-1030.jar;core.jar;resolver.jar;defaultAdaptor.jar;eclipseAdaptor.jar;console.jar;org.eclipse.ui_3.4.2.M20090204-0800.jar;org.eclipse.swt.win32.win32.x86_64_3.4.1.v3452b.jar;org.eclipse.equinox.common_3.4.0.v20080421-2006.jar;org.eclipse.swt_3.4.2.v3452b.jar;org.eclipse.jface_3.4.2.M20090107-0800.jar;commons-logging-1.2.jar com.bluejungle.destiny.appdiscovery.ApplicationDiscovery
