@echo off
set JAVA=java
%JAVA% -cp "%~dp0/*;%~dp0modules/*;%~dp0libs/*" com.annimon.ownlang.Main --file %1
pause