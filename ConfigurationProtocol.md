**To be translated to english**

Mensagens de configuração:

A classe principal do Simpatrol Server, a MainDaemon, recebe dos seus clientes mensagens de configuração do ambiente cada uma contendo um atributo type que indica o tipo de mensagem. Estas mensagens podem ser criadas pelas classes do pacote control.configuration para os clientes desenvolvidos na linguagem java. As mensagens são as seguintes:


Criação do ambiente – Mensagem que cria o ambiente principal da simulação, contém no seu corpo um documento XML do tipo environment com o grafo da simulação e as sociedades com os agentes o tipo de mensagem é “0”. É a a primeira mensagem que deve ser enviada pelo cliente.

Mensagem protocolo: 

&lt;configuration type="0"&gt;



&lt;environment&gt;



<Dados e atributos do ambiente>



&lt;/environment&gt;



&lt;/configuration&gt;




Criação de Agente – Mensagem utilizada para criar um novo agente no ambiente de simulação, no seu corpo comtém um elemento do tipo agente com a definição do agente a ser criado. O atributo type da mensagem deve ser 1 e no parameter deve ser passado o id da sociedade onde o agente deve ser criado.

Mensagem protocolo: <configuration type="1" parameter=”id da sociedade”><agent “..atributos..”>

“Elemento agente”



Unknown end tag for &lt;/agent&gt;





Unknown end tag for &lt;/configuration&gt;




Morte de um Agente – Mensagem que mata um agente no ambiente. O atributo type deve possuir valor 4 e no atributo parameter deve ser passado o id do agente que deve morrer. O corpo da mensagem deve ser vazio.

Mensagem do protocolo: <configuration type="4" parameter=”id do agente que deve morrer”>

Unknown end tag for &lt;/configuration&gt;




Criação de Métrica – Mensagem que cria um serviço de envio de uma métrica para um cliente. O atributo type deve ser “2” e o atributo parameter deve conter o número de segundos na qual uma métrica deve ser coletada. No corpo vêm um elemento do tipo métrica que na implementação atual possui quatro tipos: MEAN\_INSTANTANEOUS\_IDLENESS,MAX\_INSTANTANEOUS\_IDLENESS, MEAN\_IDLENESS e MAX\_IDLENESS. Ver detalhe das métricas na seção métricas.

Mensagem protocolo: 

&lt;configuration type="2" parameter="4.0"&gt;



&lt;metric type="número do tipo de métrica"/&gt;



&lt;/configuration&gt;




Mensagem resposta: 

&lt;orientation message="porta que deve receber a métrica"/&gt;




Início da Simulação – Mensagem que dá início a simulação, é normalmente a última mensagem enviada. O atributo type deve ser “3” e o atributo parameter contém o tempo da simulação em ciclos ou segundos.


Mensagem protocolo: 

&lt;configuration type="3" parameter="3000.0"/&gt;



Resposta: 

&lt;orientation/&gt;





Coleta de Eventos – Mensagem que cria um serviço de envio dos eventos da simulação. Pode ser enviado para logar os eventos da simulação ou exibir a simulação em um visualizador gráfico.

Mensagem protocolo: 

&lt;configuration type="5"&gt;

<

&lt;/configuration&gt;



Criação de Agente – Mensagem utilizada para criar um novo agente no ambiente de simulação, no seu corpo comtém um elemento do tipo agente com a definição do agente a ser criado. O atributo type da mensagem deve ser 1 e no parameter deve ser passado o id da sociedade onde o agente deve ser criado.

Mensagem protocolo: <configuration type="1" parameter=”id da sociedade”><agent “..atributos..”>

“Elemento agente”



Unknown end tag for &lt;/agent&gt;





Unknown end tag for &lt;/configuration&gt;




Morte de um Agente – Mensagem que mata um agente no ambiente. O atributo type deve possuir valor 4 e no atributo parameter deve ser passado o id do agente que deve morrer. O corpo da mensagem deve ser vazio.

Mensagem do protocolo: <configuration type="4" parameter=”id do agente que deve morrer”>

Unknown end tag for &lt;/configuration&gt;




Criação de Métrica – Mensagem que cria um serviço de envio de uma métrica para um cliente. O atributo type deve ser “2” e o atributo parameter deve conter o número de segundos na qual uma métrica deve ser coletada. No corpo vêm um elemento do tipo métrica que na implementação atual possui quatro tipos: MEAN\_INSTANTANEOUS\_IDLENESS,MAX\_INSTANTANEOUS\_IDLENESS, MEAN\_IDLENESS e MAX\_IDLENESS. Ver detalhe das métricas na seção métricas.

Mensagem protocolo: 

&lt;configuration type="2" parameter="4.0"&gt;



&lt;metric type="número do tipo de métrica"/&gt;



&lt;/configuration&gt;




Mensagem resposta: 

&lt;orientation message="porta que deve receber a métrica"/&gt;




Início da Simulação – Mensagem que dá início a simulação, é normalmente a última mensagem enviada. O atributo type deve ser “3” e o atributo parameter contém o tempo da simulação em ciclos ou segundos.


Mensagem protocolo: 

&lt;configuration type="3" parameter="3000.0"/&gt;



Resposta: 

&lt;orientation/&gt;





Coleta de Eventos – Mensagem que cria um serviço de envio dos eventos da simulação. Pode ser enviado para logar os eventos da simulação ou exibir a simulação em um visualizador gráfico.

Mensagem protocolo: 

&lt;configuration type="5"&gt;

<

&lt;/configuration&gt;




Simpatrol Eventos


O simpatrol possui duas categorias de eventos: de configuração e eventos de ação. Os eventos de configuração são enviados por um cliente para o servidor por meio de mensagens XML. Os tipos de eventos do simpatrol estão definidos na classe EventTypes e são os seguintes: