Author : Cyril Poulet


List of changes - 2011/06/01

Added the time perception with code -1.
Agents with the permission for perception type -1 are able to get the internal time of the simulator.
Inactive agents still have this perception, if they have the permission for it.

Added 3 actions : 
	- activate		(action type 10, event code 12). Message : <action type="10" society_id="the_id_of_the_society_to_activate_in"/>
	- deactivate		(action type 11, event code 13). Message : <action type="11"/> 
	- change society	(action type 12, event code 14). Message : <action type="12" society_id="the_id_of_the_society_to_go_to"/>
These actions do not require permission, but are only possible to seasonal agents.


The Environment now has an extra OpenSociety called "InactiveSociety".


Added the possibility for the Seasonal Agents (i.e. agents created in open societies) to :
	- deactivate : the agent stops perceiving (except the time perception, for synchronization purposes) and cannot act, except for the "activate" action. It is removed from its current society and put in the InactiveSociety.
	- activate : the agent is removed from the InactiveSociety and put in the given society, provided that it is an open society. It regains all its perceptions and actions permissions are restored.
	- change society : the agent is removed from its current society and put in the given society, provided that it is an open society. Its perceptions and actions permissions do not change.




Test :
	- tested on cycled simulation


*************************************************************************************************************************
*************************************************************************************************************************

List of changes - 2011/06/06
Added action : 
	- send message to	(action type 8, event code 15). Message : <action type="8" target_agent="target_agent_id" message="my message!"/>

Goal : ease the communication. This action is only available to communicate between 2 agents OF THE SAME SOCIETY.

The internal representation of the message (SendMessageAction) has a field "sender" wich is filled by the ActionDaemon, but so far not
exploited later by the perception daemon (i.e. the receiving agent does not know the sender).

Test :
	- tested on cycled simulation

Questions :
	- is it interesting to know where the message comes from ? how can we implement it ?
	- is it interesting to add a send_message_to_agent_in_another_society action ? (like the difference between Broadcast and BroadcastSociety)
