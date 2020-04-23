package parallel_ea_ga

import groovyJCSP.ChannelOutputList
import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EAGA_Root<T> implements CSProcess{

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
      T epData = data as T
      // see if there is a file to read as part of individual creation
      if ( epData.fileName != ""){
        // file to read and store lines in fileLines
//        println "Reading data file: ${epData.fileName} "
        new File(epData.fileName).eachLine {String line -> epData.fileLines << line }
        epData.&"$epData.processFile"()
      }
      // send data to nodes
      for (i in 0 ..< nodes) toNodes[i].write(epData)
      // wait for response from nodes indicating the population has been created
      for (i in 0 ..< nodes) fromNodes.read()
      // now sort the population
      epData.&"$epData.sortMethod"()  // should check return value
      if (printGeneration) {
        generationTime = System.currentTimeMillis()
        println "Generation - ${epData.generations} : " +
            "Fitness = ${epData.population[epData.first].getFitness()}, " +
            "InitTime= ${generationTime - previousGenerationTime}"
        previousGenerationTime = generationTime
      }
      // now start evolution loop testing for convergence or too many generations
      while (!(epData.&"$epData.convergence"() ) && ( epData.generations <= generationLimit)){
        epData.generations += 1
        // send loop again signal to nodes
        for (i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
        // wait for all nodes to have completed
        for (i in 0 ..< nodes) fromNodes.read()
        // now sort the population
        epData.&"$epData.sortMethod"()  // should check return value
        if (printGeneration) {
          generationTime = System.currentTimeMillis()
          println "Generation - ${epData.generations} : " +
              "Fitness = ${epData.population[epData.first].getFitness()}, " +
              "Cycle Time= ${generationTime - previousGenerationTime}"
          previousGenerationTime = generationTime
        }
      }
      epData.solutionFound = (epData.generations <= generationLimit)
      long endTime = System.currentTimeMillis()
      epData.timeTaken = endTime - startTime
      output.write(epData)
      data = input.read()
    }
    // terminate nodes
    for (i in 0 ..< nodes) toNodes[i].write(new UniversalTerminator())
    output.write(data)  // UT
  }
}
