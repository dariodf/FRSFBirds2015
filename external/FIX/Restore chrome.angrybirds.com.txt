@echo off
SET origen="Application Cache"
SET destino="%userprofile%\AppData\Local\Google\Chrome\User Data\Default\Application Cache"

echo SUPER MEGA .BAT QUE SOLUCIONA Y HACE ANDAR EL ANGRY BIRDS EN GOOGLE CHROME

echo.
echo Matando proceso chrome.exe
taskkill /F /IM chrome.exe

echo.
echo Borrando Application Cache
rd /S %destino%

echo.
echo Copiando nuevo Application Cache
XCOPY %origen% %destino% /E

echo.
pause
cls