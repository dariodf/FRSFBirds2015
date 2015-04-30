@echo off 
cls
echo **************************
echo *****-=[Angry Bird]=-*****
echo **************************
echo.
D:
cd D:\Users\Emilio J A\Documents\UTN - FRSF\Inteligencia Artificial 2015\Angry Birds\workspace\FRSFBirds2015
echo Server iniciado
::java -jar ABServer.jar &
::echo.
::cd D:\Users\Emilio J A\Documents\UTN - FRSF\Inteligencia Artificial 2015\Angry Birds\workspace\FRSFBirds2015\src\ab\demo
echo Complilado e Iniciando Juego
::javac MainEntry.java
::java MainEntry -nasc 127.0.0.1
 ant -d build
 ant jar
 java -jar ABSoftware.jar
pause