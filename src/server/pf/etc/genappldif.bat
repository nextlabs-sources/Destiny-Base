@echo off
..\java\jre\bin\java.exe -noverify -Djava.library.path=common/dll -jar genappldif/genappldif.jar %*
