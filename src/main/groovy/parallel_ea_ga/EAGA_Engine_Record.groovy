package parallel_ea_ga

import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Engine_Record<T> implements CSProcess{
/*
Traditional EA/GA algorithm             Parallel Version
                                              Root                                     Node(s)
                                       read in initialised data
START                                                    -- send data reference -->
Generate the initial population                                                         Create Individuals
Compute fitness                                                                         Evaluate Fitness
REPEAT                                                       <-- done --
    Selection                             sort population
    Crossover                             if converged
    Mutation                              write data else    ---- go -->
    Compute fitness                                          ^                            Crossover
UNTIL population has converged                               |                            Mutate
STOP                                                         |                            Evaluate Fitness
                                                             |    <-- done --
                                          sort population    |
                                          if converged       |
                                          write data  else --|

in the parallel version each node will undertake the specified operations on its partition of the data
the sort will order all the individuals, based on their fitness property into ascending order
*/

  ChannelInput input
  ChannelOutput output
  int nodes = 0
  boolean printGeneration = false
  int generationLimit = 250000

  @Override
  void run() {
    assert nodes > 0: "EAEngine: number of nodes must be greater than zero"
    def nodesToRoot = Channel.any2one()
    def rootToNodes = Channel.one2oneArray(nodes)
    def toNodes = new ChannelOutputList(rootToNodes)
    def network = []
    network << new EAGA_Root<T>(
        input: input,
        output: output,
        printGeneration: printGeneration,
        generationLimit: generationLimit,
        toNodes: toNodes,
        fromNodes: nodesToRoot.in()
    )
    for (n in 0 ..< nodes){
      network << new EAGA_Node_Record<T>(
          fromRoot: rootToNodes[n].in(),
          toRoot: nodesToRoot.out(),
          nodeId: n,
      )
    }
    new PAR(network).run()

  }
}