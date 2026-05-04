@echo off
echo ===========================================
echo Running ALL Hospital Management System Tests
echo ===========================================
echo.

if not exist "bin" mkdir bin
echo Cleaning old compiled files...
rmdir /s /q bin 2>nul
mkdir bin

:: Classpath including JUnit and the MySQL Driver
set CP=bin;lib\junit-platform-console-standalone-1.0.0.jar;lib\mysql-connector-j-9.7.0.jar

echo Compiling all project classes (model, util, repository, services)...
:: ADDED services\*.java HERE
javac -d bin model\*.java util\*.java repository\*.java services\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile core project classes!
    pause
    exit /b 1
)

echo Compiling all tests...
javac -cp "%CP%" -d bin Test\model\*.java Test\repository\*.java Test\services\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile tests!
    pause
    exit /b 1
)

echo.
echo Running all tests...
echo ===========================================
java -cp "%CP%" org.junit.platform.console.ConsoleLauncher --class-path bin --scan-class-path --details verbose

echo.
echo ===========================================
echo Tests Complete!
echo ===========================================
pause