@echo off

set FX_LIB=javafx-sdk-25.0.3\lib
set SRC=src\main\java
set OUT=out

if not exist %OUT% mkdir %OUT%

javac --module-path %FX_LIB% --add-modules javafx.controls -d %OUT% -sourcepath %SRC% %SRC%\Main.java

if errorlevel 1 (
    pause
    exit /b
)

java --enable-native-access=javafx.graphics --module-path %FX_LIB% --add-modules javafx.controls -cp %OUT%;src\main\resources Main

pause