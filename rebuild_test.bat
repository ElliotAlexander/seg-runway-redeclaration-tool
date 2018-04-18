@echo off

ECHO. && ECHO Starting Tests && ECHO.

ECHO Test 1: Database rebuild && ECHO Deleting Database
@RD /S /Q "C:\Users\%USERNAME%\Runway_Redeclaration_Tool\db\"
ECHO. && ECHO Running application && ECHO Sleeping for 10 seconds... && ECHO.
start "" "%comspec%" /c /B RunwayRedeclarationTool.exe
SLEEP 10
taskkill /F /IM RunwayRedeclarationTool.exe
ECHO Closing application
IF EXIST "C:\Users\%USERNAME%\Runway_Redeclaration_Tool\db\airports.db" (
    ECHO [Database Rebuild Test: PASS]
) ELSE (
    ECHO [Database Rebuild Test: FAIL]
)

ECHO Test 2: Config rebuild && ECHO Deleting Config File
@RD /S /Q "C:\Users\%USERNAME%\Runway_Redeclaration_Tool\config.txt"
ECHO. && ECHO Running application && ECHO Sleeping for 10 seconds... && ECHO.
start "" "%comspec%" /c /B RunwayRedeclarationTool.exe
SLEEP 10
taskkill /F /IM RunwayRedeclarationTool.exe
ECHO Closing application
IF EXIST "C:\Users\%USERNAME%\Runway_Redeclaration_Tool\config.txt" (
    ECHO. && ECHO [Config Rebuild Test: PASS]
) ELSE (
    ECHO. && ECHO [Config Rebuild Test: FAIL]
)
