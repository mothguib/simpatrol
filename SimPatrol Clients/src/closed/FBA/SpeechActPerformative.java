package closed.FBA;

/**
 * Performatives of the messages. The flow of successive messages is
 * 
 * 
 * 								reject
 * 				propose   ->  	accept
 * Inform ->  
 * 				refuse
 * 
 * or
 * 
 * Enter  ->  propose -> accept
 * Quit
 * 
 * 
 * @author pouletc
 *
 */
public enum SpeechActPerformative {
	INFORM, 
	REJECT, 
	PROPOSE,
	ACCEPT, 
	REFUSE, 
	NOT_UNDERSTOOD,
	ENTER,
	QUIT
}
