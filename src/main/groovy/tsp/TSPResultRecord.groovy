package tsp

import groovyParallelPatterns.DataClass

class TSPResultRecord extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual population
  int collector(TSPPopulationRecord data){
    println "Result: $resultNumber = Solution Found is  ${data.solutionFound}; " +
        "Time: ${data.timeTaken}; " +
        "Generations: ${data.generations}; " +
//        "${data.population[data.first].route} \n" +
//        "Fitness is ${data.population[data.first].getFitness()}\n"+
        "Seeds: ${data.seeds}\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}