package tsp

import groovyParallelPatterns.DataClass

class TSPResult extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber, solutions
  BigDecimal bestFitness

  int initClass ( List d){
    resultNumber = 1
    solutions = 0
    bestFitness = 10000
    return completedOK
  }

  //TODO change type of input Object to individual individuals
  int collector(TSPPopulation data){
    boolean found = data.solutionFound
    BigDecimal fitness = data.individuals[data.first].getFitness()
    if (found) solutions++
    if ( fitness < bestFitness) bestFitness = fitness
    println "Result, $resultNumber, Found, $found, " +
        "Time, ${data.timeTaken}, " +
        "Generations, ${data.generations}, " +
        "Fitness, $fitness, "+
        "${data.individuals[data.first].route}," +
        "Seeds, ${data.seeds}\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    println "Found $solutions solutions with best fitness $bestFitness"
    return completedOK
  }

}
