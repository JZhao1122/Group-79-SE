@echo off 
set CLASSPATH=D:\桌面\Group-79-SE-main\lib\junit-platform-console-standalone-1.10.0.jar;D:\桌面\Group-79-SE-main\lib\json-20250107.jar;D:\桌面\Group-79-SE-main\out;D:\桌面\Group-79-SE-main\GUI;D:\桌面\Group-79-SE-main\ai_core;D:\桌面\Group-79-SE-main\service;. 
echo 姝ｅ湪杩愯JUnit娴嬭瘯... 
java -cp "%CLASSPATH%" org.junit.platform.console.ConsoleLauncher --scan-classpath 
pause 
