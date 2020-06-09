package parallel_ea_ga

import groovyParallelPatterns.UniversalRequest
import groovyParallelPatterns.UniversalResponse
import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Node<T> implements CSProcess {
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
//      int best, secondBest, worst   // subscripts in individuals of node's manipulated individuals
      int ppn = population.populationPerNode
      int lastIndex = population.lastIndex
      int start = nodeId * ppn
      int end = start + ppn -1
      for ( i in start .. end) {
        population.individuals[i].createIndividual(population, rng)
      }
      // subscripts of the parents and worst individuals used by this node
      // values depend on maximise
      int parent1, parent2, worst1, worst2
      if (population.maximise){
        parent1 = lastIndex - (nodeId * 2)
        parent2 = parent1 - 1
        worst1 = nodeId
        worst2 = population.nodes + nodeId
      } else {
        parent1 = nodeId * 2
        parent2 = parent1 + 1
        worst1 = lastIndex - nodeId
        worst2 = lastIndex - population.nodes - nodeId
      }

      // children locations do not depend on maximise
      int child1, child2            // subscripts of children used in crossover
      child1 = lastIndex + (nodeId * 2) + 1
      child2 = child1 + 1
//      println "$nodeId: $parent1, $parent2, $worst1, $worst2, $child1, $child2"
      population.individuals[child1].createIndividual(population, rng)
      population.individuals[child2].createIndividual(population, rng)
      // individuals now set up
      toRoot.write(new UniversalSignal()) // tell root that node has finished initialisation
      data = fromRoot.read()              // read signal from root
      // start of main evolution loop
      boolean modified
      while ((data instanceof UniversalSignal) ||( data instanceof UniversalRequest)) {
        if (data instanceof UniversalSignal) {
          // evaluating next generation
          modified = false
          if (rng.nextDouble() < population.crossoverProbability) {
            population.&"$population.crossover"(parent1, parent2, child1, child2, rng)
            modified = true
          } else population.&"$population.copyParentsToChildren"(parent1, parent2, child1, child2)
          if (rng.nextDouble() < population.mutationProbability) {
            population.individuals[child1].mutate(rng)
            population.individuals[child2].mutate(rng)
            modified = true
          }
          if (modified) {
            population.individuals[child1].evaluateFitness(population)
            population.individuals[child2].evaluateFitness(population)
            population.&"$population.combineChildren"(parent1, parent2, worst1, worst2, child1, child2)
          }
          toRoot.write(new UniversalSignal())
        }
        else {
          // replacing children with new individuals
          assert data instanceof UniversalRequest : "Node expected UniversalRequest but not read"
//          println "$nodeId replacing child1 $child1"
          population.individuals[child1].createIndividual(population, rng)
//          println "$nodeId now replacing child2 $child2"
          population.individuals[child2].createIndividual(population, rng)
//          println "$nodeId replaced $child1 and $child2"
          toRoot.write(new UniversalResponse())
        }
        data = fromRoot.read()
      } // main loop
    } // processing data inputs
    // data is Universal Terminator
    assert data instanceof UniversalTerminator: "EAGA_Node expected UniversalTerminator"
  }
}
