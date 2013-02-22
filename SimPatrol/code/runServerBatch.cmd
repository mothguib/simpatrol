@echo off

set JAVA_EXEC="D:\Program Files (x86)\Java\jdk1.7.0_07\bin\java.exe"
set USE_IPC=true

FOR /L %%i IN (1,1,200) DO (
	echo EXECUTION #%%i OF SIMPATROL WITH PORT 5000
	%JAVA_EXEC% -d32 -cp bin\;lib\aspectjrt.jar;lib\colt.jar view.gui.SimPatrolGUI 0.005 false 5000 %USE_IPC%
)