@ECHO OFF

set JAVA_EXEC="D:\Program Files (x86)\Java\jdk1.7.0_07\bin\java.exe"

echo Iniciando o SimPatrol em sequencia

cd ..
cd "New SimPatrol"
start runServerBatch
cd ..

cd "New Agent Library"

echo Pressione para iniciar os agentes em sequencia

pause

FOR %%f in (_experiments\*.xml) DO (
	ECHO Experiment %%f
	%JAVA_EXEC% -cp bin\;_oldclients_bin _tests.RunAgentsConfigFile %%f
)

pause

