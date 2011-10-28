/***************************************
 * 
 * Flexible Bidder Agent
 * 
 * 
 * Adapted from Machado, Almeida & Menezes' C++ version
 * 
 * @author Cyril Poulet
 ***************************************/
 
 
 This package contains the code for the flexible bidder agent as described by Machado, Almeida & Menezes in 2006
 and transcribed from their C++ code.
 
 It has however been modified to allow the use with Simpatrol : it now communicates asynchronously.
 This induces :
  - a need to keep track of proposed nodes in transactions not yet finished
  - not proposing said nodes as long as the related transactions are not finished.
 This is what the TransactionNodes class is for.
 
 This causes the auction process to be suboptimal, and leads to performances that are not as good as published.