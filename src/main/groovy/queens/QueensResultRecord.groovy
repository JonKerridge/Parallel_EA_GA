package queens

import groovyParallelPatterns.DataClass

class QueensResultRecord extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual individuals
  int collector(QueensPopulationRecord data){
    String seedString = ""
    data.seeds.each( seed -> seedString = seedString + "$seed, ")
    println "Result, $resultNumber, Found, ${data.solutionFound}, " +
        "Time, ${data.timeTaken}, " +
        "Generations, ${data.generations}, " +
        "Fitness, ${data.individuals[data.first].getFitness()}, "+
//        "${data.individuals[data.first].board} \n" +
        "Seeds, $seedString\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
