@ECHO OFF

set JAVA_EXEC="C:\Program Files (x86)\Java\jdk1.7.0_09\bin\java.exe"

echo Iniciando o SimPatrol em sequencia

cd ..
cd "SimPatrol"
start runServerBatch
cd ..

cd "SimPatrol New Clients"

echo Pressione para iniciar os agentes em sequencia

pause

FOR %%f in (_experiments\*.xml) DO (
	ECHO Experiment %%f
	%JAVA_EXEC% -cp bin\;_oldclients_bin _tests.RunAgentsConfigFile %%f
)

echo Fim da execucao
pause

