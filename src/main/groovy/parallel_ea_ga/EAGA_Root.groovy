package parallel_ea_ga

import groovy_jcsp.ChannelOutputList
import groovy_parallel_patterns.DataClassInterface
import groovy_parallel_patterns.UniversalRequest
import groovy_parallel_patterns.UniversalResponse
import groovy_parallel_patterns.UniversalSignal
import groovy_parallel_patterns.UniversalTerminator
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
  int generationLimit = 100000

  @Override
  void run() {
    int nodes = toNodes.size()
    int loopCount = 0
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
      assert population.&"$population.sortMethod"(true)  == DataClassInterface.completedOK :
        "initial population sort failed"
      if (printGeneration) {
        generationTime = System.currentTimeMillis()
        println "Generation - ${population.generations} : " +
            "Fitness = ${population.individuals[population.first].getFitness()}, " +
            "InitTime= ${generationTime - previousGenerationTime}"
        previousGenerationTime = generationTime
      }
      loopCount = 0
      // now start evolution loop testing for convergence or too many generations
      while (!(population.&"$population.convergence"() ) && ( population.generations <= generationLimit)){
        population.generations += 1
        loopCount += 1
        if (loopCount == population.replaceCount){
          loopCount = 0
          // send replace children signal to nodes
//          println "Root: sending signal to nodes to create new children"
          for (i in 0 ..< nodes) toNodes[i].write(new UniversalRequest())
//          println "Root: nodes should be creating new children"
          // wait for all nodes to have completed by reading UniversalResponses
          for (i in 0 ..< nodes) fromNodes.read()
//            assert fromNodes.read() instanceof UniversalResponse :
//              "root expecting Universal Response after total child replacement"

          // now sort the individuals
//          println "root now sorting replacements"
          assert population.&"$population.sortMethod"(false)  == DataClassInterface.completedOK :
              "sort after child replacement failed"
//          population.&"$population.sortMethod"(false)
        } // end of loopCount test
        // send loop again signal to nodes
        for (i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
        // wait for all nodes to have completed by reading UniversalSignals
        for (i in 0 ..< nodes) fromNodes.read()
//          assert fromNodes.read() instanceof UniversalSignal :
//              "root expecting Universal Signal after generation loop"

        // now sort the individuals
        assert population.&"$population.sortMethod"(true)  == DataClassInterface.completedOK :
            "sort at end of generation loop failed"
//        population.&"$population.sortMethod"(true)
        if (printGeneration) {
          generationTime = System.currentTimeMillis()
          println "Generation - ${population.generations} : " +
              "Fitness = ${population.individuals[population.first].getFitness()}, " +
              "Cycle Time = ${generationTime - previousGenerationTime} " +
              "Loop count = $loopCount"
          previousGenerationTime = generationTime
        } // end of print generation
      } //end of main while loop
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
