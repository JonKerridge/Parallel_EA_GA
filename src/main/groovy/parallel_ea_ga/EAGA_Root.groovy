package parallel_ea_ga

import groovyJCSP.ChannelOutputList
import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Root<T> implements CSProcess{
// T is based on EAGA_Population
  ChannelInput input
  ChannelOutput output
  ChannelOutputList toNodes
  ChannelInput fromNodes
  boolean printGeneration = false
  int generationLimit = 250000

  @Override
  void run() {
    int nodes = toNodes.size()
    Object data
    data = input.read()
    long previousGenerationTime, generationTime
    previousGenerationTime = System.currentTimeMillis()
    while (!(data instanceof UniversalTerminator)){
      long startTime = System.currentTimeMillis()
      T population = data as T
      // see if there is a file to read as part of individual creation
      if ( population.fileName != ""){
        // file to read and store lines in fileLines
//        println "Reading data file: ${population.fileName} "
        new File(population.fileName).eachLine {String line -> population.fileLines << line }
        population.&"$population.processFile"()
      }
      // send data to nodes
      for (i in 0 ..< nodes) toNodes[i].write(population)
      // wait for response from nodes indicating the individuals has been created
      for (i in 0 ..< nodes) fromNodes.read()
      // now sort the individuals
      population.&"$population.sortMethod"()  // should check return value
      if (printGeneration) {
        generationTime = System.currentTimeMillis()
        println "Generation - ${population.generations} : " +
            "Fitness = ${population.individuals[population.first].getFitness()}, " +
            "InitTime= ${generationTime - previousGenerationTime}"
        previousGenerationTime = generationTime
      }
      // now start evolution loop testing for convergence or too many generations
      while (!(population.&"$population.convergence"() ) && ( population.generations <= generationLimit)){
        population.generations += 1
        // send loop again signal to nodes
        for (i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
        // wait for all nodes to have completed
        for (i in 0 ..< nodes) fromNodes.read()
        // now sort the individuals
        population.&"$population.sortMethod"()  // should check return value
        if (printGeneration) {
          generationTime = System.currentTimeMillis()
          println "Generation - ${population.generations} : " +
              "Fitness = ${population.individuals[population.first].getFitness()}, " +
              "Cycle Time= ${generationTime - previousGenerationTime}"
          previousGenerationTime = generationTime
        }
      }
      population.solutionFound = (population.generations <= generationLimit)
      long endTime = System.currentTimeMillis()
      population.timeTaken = endTime - startTime
      output.write(population)
      data = input.read()
    }
    // terminate nodes
    for (i in 0 ..< nodes) toNodes[i].write(new UniversalTerminator())
    output.write(data)  // UT
  }
}
