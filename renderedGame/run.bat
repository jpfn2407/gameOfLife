@ECHO OFF
mode con:cols=100 lines=20
echo Conway Game of Life.
echo Made by jpfn2407
echo Github: https://github.com/jpfn2407

if exist "C:\Program Files\Java\jdk1.8.0_281\bin" goto jdk8
if exist "C:\Program Files\Java\jre1.8.0_281\bin" goto jre8

echo Java jdk8 or jre8 not found in default path "C:\Program Files\Java\". 
echo Please install either Java jdk8 or jre8 from https://www.java.com/download/manual.jsp
goto commandEnd

:jdk8
    "C:\Program Files\Java\jdk1.8.0_281\bin\java.exe" -jar gameOfLife.jar
    goto commandEnd

:jre8
    "C:\Program Files\Java\jre1.8.0_281\bin\java.exe" -jar gameOfLife.jar
    goto commandEnd

:commandEnd
    echo Ending program.
    pause