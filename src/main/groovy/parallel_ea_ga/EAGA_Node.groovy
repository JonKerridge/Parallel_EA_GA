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
      T populationData = (T)data
      if (populationData.seeds[nodeId] != null) {
        long seed = (long)populationData.seeds[nodeId]
        rng = new Random(seed)
      }
      else {
        // create a seed and record it
        // means seed values can be output
        // each instance of the data will have different seeds
        long seed = System.nanoTime()
        populationData.seeds[nodeId] = seed
        rng = new Random(seed)
      }
      int best, secondBest, worst   // subscripts in population of node's manipulated individuals
      int child1, child2            // subscripts of children used in crossover
      int ppn = populationData.populationPerNode
      int lastIndex = populationData.lastIndex
      double mutateProbability = populationData.mutationProbability
      if (populationData.maximise){
        worst = nodeId * ppn
        best = worst + ppn - 1
        secondBest = best - 1
        for ( i in worst .. best) {
          populationData.population[i].createIndividual(populationData, rng)
        }
      }
      else { // minimising fitness
        best = nodeId * ppn
        secondBest = best + 1
        worst = best + ppn -1
        for ( i in best .. worst) {
          populationData.population[i].createIndividual(populationData, rng)
        }
      }  // setting up conditional
      // children locations do not depend on maximise
      child1 = lastIndex + (nodeId * 2) + 1
      child2 = child1 + 1
//      println "$nodeId: $best, $secondBest, $worst, $child1, $child2"
      populationData.population[child1].createIndividual(populationData, rng)
      populationData.population[child2].createIndividual(populationData, rng)
      // population now set up
      toRoot.write(new UniversalSignal()) // tell root that node has finished initialisation
      data = fromRoot.read()              // read signal from root
      // start of main evolution loop
      while (data instanceof UniversalSignal){
        if (rng.nextDouble() < populationData.crossoverProbability)
          populationData.&"$populationData.crossover"(best, secondBest, worst, child1, child2, rng)
        if (rng.nextDouble() < populationData.mutationProbability)
          populationData.population[child1].mutate( rng)
        if (rng.nextDouble() < populationData.mutationProbability)
          populationData.population[child2].mutate( rng)
        populationData.population[child1].evaluateFitness()
        populationData.population[child2].evaluateFitness()
        populationData.&"$populationData.combineChildren"(best, secondBest, worst, child1, child2)
        toRoot.write(new UniversalSignal())
        data = fromRoot.read()
      } // main loop
    } // processing data inputs
  }
}
