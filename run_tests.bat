@echo off 
set CLASSPATH=D:\����\Group-79-SE-main\lib\junit-platform-console-standalone-1.10.0.jar;D:\����\Group-79-SE-main\lib\json-20250107.jar;D:\����\Group-79-SE-main\out;D:\����\Group-79-SE-main\GUI;D:\����\Group-79-SE-main\ai_core;D:\����\Group-79-SE-main\service;. 
echo 正在运行JUnit测试... 
java -cp "%CLASSPATH%" org.junit.platform.console.ConsoleLauncher --scan-classpath 
pause 
