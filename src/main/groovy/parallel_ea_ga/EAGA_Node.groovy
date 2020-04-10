package parallel_ea_ga

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Node<T> implements CSProcess {
  ChannelInput fromRoot
  ChannelOutput toRoot
  int nodeId =-1

  @Override
  void run() {

    Random rng
    Object data
    data = fromRoot.read()
    while (!(data instanceof UniversalTerminator)){
      // processing a new population instance
      T epData = (T)data
      if (epData.seeds != null) {
        long seed = (long)epData.seeds[nodeId]
        rng = new Random(seed)
      }
      else
        rng = new Random()
      int best, secondBest, worst   // subscripts in population of node's manipulated individuals
      int child1, child2            // subscripts of children used in crossover
      int ppn = epData.populationPerNode
      int lastIndex = epData.lastIndex
      double mutateProbability = epData.mutationProbability
      if (epData.maximise){
        worst = nodeId * ppn
        best = worst + ppn - 1
        secondBest = best - 1
        for ( i in worst .. best) {
          epData.population[i].createIndividual(epData, rng)
        }
      }
      else { // minimising fitness
        best = nodeId * ppn
        secondBest = best + 1
        worst = best + ppn -1
        for ( i in best .. worst) {
          epData.population[i].createIndividual(epData, rng)
        }
      }  // setting up conditional
      // children locations do not depend on maximise
      child1 = lastIndex + (nodeId * 2) + 1
      child2 = child1 + 1
//      println "$nodeId: $best, $secondBest, $worst, $child1, $child2"
      epData.population[child1].createIndividual(epData, rng)
      epData.population[child2].createIndividual(epData, rng)
      // population now set up
      toRoot.write(new UniversalSignal()) // tell root that node has finished initialisation
      data = fromRoot.read()              // read signal from root
      // start of main evolution loop
      while (data instanceof UniversalSignal){
        epData.&"$epData.crossover"(best, secondBest, worst, child1, child2, mutateProbability, rng)
        toRoot.write(new UniversalSignal())
        data = fromRoot.read()
      } // main loop
    } // processing data inputs
  }
}
