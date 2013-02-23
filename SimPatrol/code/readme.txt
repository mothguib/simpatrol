This project holds the SimPatrol itself, which is a server simulator for the timed 
multiagent patrolling problem. The main class is "view.gui.SimPatrolGUI".

Patrolling agents are controlled by clients that connect to this server, communicating 
with it in XML. (Clients may be implemented in any language).

The following projects have agents implemented:
- SimPatrol New Clients (library for agents' creation, with some agents implemented) 
- SimPatrol Clients (old agents' implementations, to be ported to the new library)