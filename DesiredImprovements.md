## Major Improvements ##

  1. Faster execution
  1. Viewer



---

## Minor Improvements ##

  1. Add event VISIT\_COMPLETED
  1. Add event to notify the clients about the end of the simulation
  1. Implement different "visit times" per node
  1. Change (in the code and in the messages) all references to "environment" to something like "simulation setting" (because the environment is simply the graph)
  1. Include the time of simulation in the "environment" (simulation setting) - useful for calculating metrics
  1. Correct clients' finalization at the end of simulation (not all of them seem to be ending correctly)