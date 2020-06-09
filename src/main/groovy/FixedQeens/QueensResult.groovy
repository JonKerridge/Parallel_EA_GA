package FixedQeens

import groovyParallelPatterns.DataClass

class QueensResult extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber
  static int solutions
  static int instances
  PrintWriter outWriter

  int initClass ( List d){
    resultNumber = 1
    solutions = 0
    instances = 0
    outWriter = d[0] as PrintWriter
    return completedOK
  }

  //TODO change type of input Object to individual individuals
  int collector(FixedQeens.QueensPopulation data){
    if (data.solutionFound) solutions += 1
    instances = data.instances
    String seedString = ""
    data.seeds.each( seed -> seedString = seedString + "$seed, ")
    println "Result $resultNumber Found ${data.solutionFound} " +
        "Time ${data.timeTaken} " +
        "Generations ${data.generations} " +
        "Fitness ${data.individuals[data.first].getFitness()} "+
        "Board ${data.individuals[data.first].board} " +
        "Seeds $seedString\n"
    outWriter.println("$resultNumber, ${data.solutionFound}, ${data.timeTaken}, ${data.generations}, " +
    "${data.individuals[data.first].getFitness()}, , ${data.individuals[data.first].board}, , $seedString")
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    outWriter.println "$solutions out of $instances found"
    return completedOK
  }

}
