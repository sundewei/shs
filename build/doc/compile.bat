@ECHO off
:input
set INUMBER=
set /P INUMBER=Please enter your SAP I Number: %=%
if "%INUMBER%"=="" goto input
echo Your I Number is: %INUMBER% and it will be used to create the MapReduce jar file...
echo.
cd c:\2012SapHadoop

echo Compiling LogProcessor.java
javac -cp commons-csv-20070730.jar;commons-io-2.0.1.jar;commons-logging-1.0.4.jar;hadoop-core-0.20.2-cdh3u4.jar;log4j-1.2.15.jar LogProcessor.java
echo.
echo Creating %INUMBER%.jar
jar cf %INUMBER%.jar LogProcessor*.*
echo.
echo Uploading %INUMBER%.jar to SAP Hadoop master node (LLBPAL36)
pscp %INUMBER%.jar hadoop@llbpal36.pal.sap.corp:/home/hadoop/