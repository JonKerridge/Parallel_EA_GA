package queens

import groovyParallelPatterns.DataClass
import tsp.TSPPopulation

class QueensResult extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual population
  int collector(QueensPopulation data){
    String seedString = ""
    data.seeds.each( seed -> seedString = seedString + "$seed, ")
    println "Result, $resultNumber, Found, ${data.solutionFound}, " +
        "Time, ${data.timeTaken}, " +
        "Generations, ${data.generations}, " +
        "Fitness, ${data.population[data.first].getFitness()}, "+
//        "${data.population[data.first].board} \n" +
        "Seeds, $seedString\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
