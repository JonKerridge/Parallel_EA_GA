package parallel_ea_ga

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Node_Record<T> implements CSProcess {
  // T is based on EAGA_Population

  ChannelInput fromRoot
  ChannelOutput toRoot
  int nodeId =-1

  @Override
  void run() {

    Random rng
    Object data
    data = fromRoot.read()
    while (!(data instanceof UniversalTerminator)){
      // processing a new individuals instance
      T population = data as T
      if (population.seeds[nodeId] != null) {
        long seed = (long)population.seeds[nodeId]
        rng = new Random(seed)
      }
      else {
        // create a seed and record it
        // means seed values can be output
        // each instance of the data will have different seeds
        long seed = System.nanoTime()
        population.seeds[nodeId] = seed
        rng = new Random(seed)
      }
      int best, secondBest, worst   // subscripts in individuals of node's manipulated individuals
      int child1, child2            // subscripts of children used in crossover
      int ppn = population.populationPerNode
      int lastIndex = population.lastIndex
      if (population.maximise){
        worst = nodeId * ppn
        best = worst + ppn - 1
        secondBest = best - 1
        for ( i in worst .. best) {
          population.individuals[i].createIndividual(population, rng)
          population.individuals[i].updateNodesVisited(nodeId)
        }
      }
      else { // minimising fitness
        best = nodeId * ppn
        secondBest = best + 1
        worst = best + ppn -1
        for ( i in best .. worst) {
          population.individuals[i].createIndividual(population, rng)
        }
      }  // setting up conditional
      // children locations do not depend on maximise
      child1 = lastIndex + (nodeId * 2) + 1
      child2 = child1 + 1
//      println "$nodeId: $best, $secondBest, $worst, $child1, $child2"
      population.individuals[child1].createIndividual(population, rng)
      population.individuals[child2].createIndividual(population, rng)
      // individuals now set up
      toRoot.write(new UniversalSignal()) // tell root that node has finished initialisation
      data = fromRoot.read()              // read signal from root
      // start of main evolution loop
      boolean modified
      while (data instanceof UniversalSignal) {
        modified = false
        if (rng.nextDouble() < population.crossoverProbability) {
          population.&"$population.crossover"(best, secondBest, child1, child2, rng)
          modified = true
        }
        if (rng.nextDouble() < population.mutationProbability){
          population.individuals[child1].mutate( rng)
          population.individuals[child2].mutate( rng)
          modified = true
        }
        if (modified) {
          population.individuals[child1].evaluateFitness(population)
          population.individuals[child2].evaluateFitness(population)
          population.individuals[child1].updateNodesVisited(nodeId)
          population.individuals[child2].updateNodesVisited(nodeId)
          population.&"$population.combineChildren"(best, secondBest, worst, child1, child2)
        }
        toRoot.write(new UniversalSignal())
        data = fromRoot.read()
      } // main loop
    } // processing data inputs
    // data is Universal Terminator
    assert data instanceof UniversalTerminator: "EAGA_Node expected UniversalTerminator"
  }
}
