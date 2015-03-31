# Introduction #

When programming patrolling agents for the SimPatrol, it is important to know the codes for perceptions, actions and events that are used in the messages. These codes (numbers) are presented here. They are used in the "type" field of the messages.


## 1. Types of Perceptions ##

| **Perception** | **Code** |
|:---------------|:---------|
| GRAPH        | 0 |
| AGENTS       | 1 |
| STIGMAS      | 2 |
| BROADCAST    | 3 |
| SELF         | 4 |


## 2. Types of Actions ##

| **Action** | **Code** |
|:-----------|:---------|
| TELEPORT | 0 |
| GOTO     | 1 |
| VISIT    | 2 |
| BROADCAST  | 3 |
| STIGMATIZE | 4 |
| RECHARGE   | 5 |
| ATOMIC\_RECHARGE   | 6 |
| BROADCAST\_SOCIETY | 7 |

_Observations_:
  1. A goto action indicates the next vertex to go. It is automatically interpreted as a series of of teleport actions, which are used for specific displacements along the edges.
  1. The stigmatize action is related to the placement of information in a node of the graph.
  1. Missing: describe the parameters of the actions.


## 3. Types of Events ##

| **Event**        | **Code** |
|:-----------------|:---------|
| AGENT\_CREATION | 0 |
| AGENT\_DEATH    | 1 |
| AGENT\_CHANGING\_STATE   | 2 |
| AGENT\_SPENDING\_STAMINA | 3 |
| AGENT\_RECHARGING   | 4 |
| AGENT\_TELEPORTING  | 5 |
| AGENT\_VISIT        | 6 |
| AGENT\_STIGMATIZING | 7 |
| AGENT\_BROADCASTING | 8 |
| AGENT\_RECEIVING\_MESSAGE | 9 |
| NODE\_ENABLING  | 10 |
| EDGE\_ENABLING  | 11 |

_Observations_:
  1. The event "agent changing state" has as an aditional field, which indicates the agent current status, which can be JUST\_PERCEIVED (0) or JUST\_ACTED (1).
  1. Missing: describe extra fields of other events.