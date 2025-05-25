@echo off
echo 正在编译项目...

REM 设置项目路径和类路径
set PROJECT_ROOT=%~dp0
set CLASSPATH=%PROJECT_ROOT%lib\junit-platform-console-standalone-1.10.0.jar;%PROJECT_ROOT%lib\json-20250107.jar;.

REM 创建输出目录
if not exist out mkdir out

REM 分别编译各个模块，避免路径问题
echo 编译 ai_core 模块...
javac -cp "%CLASSPATH%" -d out ai_core\*.java 2>&1

echo 编译 service 模块...
javac -cp "%CLASSPATH%;out" -d out service\*.java 2>&1

echo 编译 test 模块...
javac -cp "%CLASSPATH%;out" -d out test\*.java 2>&1

REM 递归编译GUI目录下的所有Java文件
echo 编译 GUI 模块...
for /r GUI %%f in (*.java) do (
    javac -cp "%CLASSPATH%;out" -d out -sourcepath . "%%f" 2>&1
)

if %ERRORLEVEL% EQU 0 (
    echo ✓ 编译成功！
) else (
    echo ✗ 编译完成，请检查错误信息
)

pause 
