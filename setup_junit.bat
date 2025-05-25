@echo off
echo ============================================
echo          JUnit 库一键安装脚本
echo ============================================
echo.

REM 设置项目路径
set PROJECT_ROOT=%~dp0
set JUNIT_JAR=%PROJECT_ROOT%junit-platform-console-standalone-1.10.0.jar
set JSON_JAR=%PROJECT_ROOT%json-20250107.jar

REM 检查JUnit jar文件是否存在
if not exist "%JUNIT_JAR%" (
    echo 错误: 找不到JUnit jar文件: %JUNIT_JAR%
    echo 请确保 junit-platform-console-standalone-1.10.0.jar 文件在项目根目录中
    pause
    exit /b 1
)

echo ✓ 找到JUnit jar文件: %JUNIT_JAR%

REM 检查JSON jar文件是否存在
if not exist "%JSON_JAR%" (
    echo 警告: 找不到JSON jar文件: %JSON_JAR%
) else (
    echo ✓ 找到JSON jar文件: %JSON_JAR%
)

REM 创建lib目录
if not exist "%PROJECT_ROOT%lib" (
    mkdir "%PROJECT_ROOT%lib"
    echo ✓ 创建lib目录
)

REM 复制jar文件到lib目录
copy "%JUNIT_JAR%" "%PROJECT_ROOT%lib\" >nul 2>&1
if exist "%JSON_JAR%" (
    copy "%JSON_JAR%" "%PROJECT_ROOT%lib\" >nul 2>&1
)

echo ✓ jar文件已复制到lib目录

REM 设置classpath环境变量
set CLASSPATH=%PROJECT_ROOT%lib\junit-platform-console-standalone-1.10.0.jar;%PROJECT_ROOT%lib\json-20250107.jar;%PROJECT_ROOT%out;%PROJECT_ROOT%GUI;%PROJECT_ROOT%ai_core;%PROJECT_ROOT%service;.

echo.
echo ============================================
echo          JUnit 配置完成！
echo ============================================
echo.
echo 类路径已设置为:
echo %CLASSPATH%
echo.
echo 使用方法:
echo 1. 编译Java文件: javac -cp "%CLASSPATH%" YourTestClass.java
echo 2. 运行JUnit测试: java -cp "%CLASSPATH%" org.junit.platform.console.ConsoleLauncher --scan-classpath
echo.
echo 或者使用提供的快捷脚本:
echo - run_tests.bat : 运行所有测试
echo - compile_project.bat : 编译整个项目
echo.

REM 创建快捷运行脚本
call :create_run_tests_script
call :create_compile_script

echo 已创建额外的快捷脚本！
echo.
pause
exit /b 0

:create_run_tests_script
echo @echo off > run_tests.bat
echo set CLASSPATH=%PROJECT_ROOT%lib\junit-platform-console-standalone-1.10.0.jar;%PROJECT_ROOT%lib\json-20250107.jar;%PROJECT_ROOT%out;%PROJECT_ROOT%GUI;%PROJECT_ROOT%ai_core;%PROJECT_ROOT%service;. >> run_tests.bat
echo echo 正在运行JUnit测试... >> run_tests.bat
echo java -cp "%%CLASSPATH%%" org.junit.platform.console.ConsoleLauncher --scan-classpath >> run_tests.bat
echo pause >> run_tests.bat
goto :eof

:create_compile_script
echo @echo off > compile_project.bat
echo set CLASSPATH=%PROJECT_ROOT%lib\junit-platform-console-standalone-1.10.0.jar;%PROJECT_ROOT%lib\json-20250107.jar;. >> compile_project.bat
echo echo 正在编译项目... >> compile_project.bat
echo if not exist out mkdir out >> compile_project.bat
echo javac -cp "%%CLASSPATH%%" -d out GUI\*.java ai_core\*.java service\*.java test\*.java 2^>^&1 >> compile_project.bat
echo if %%ERRORLEVEL%% EQU 0 ^( >> compile_project.bat
echo     echo ✓ 编译成功！ >> compile_project.bat
echo ^) else ^( >> compile_project.bat
echo     echo ✗ 编译失败，请检查错误信息 >> compile_project.bat
echo ^) >> compile_project.bat
echo pause >> compile_project.bat
goto :eof 