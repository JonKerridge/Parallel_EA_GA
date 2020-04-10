package maxOnes

import groovyParallelPatterns.DataClass

class MaxOneResult extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual population
  int collector(MaxOnePopulation data){
      println "Result: $resultNumber = Time: ${data.timeTaken}; " +
          "Generations: ${data.generations}\n " +
          "Seeds: ${data.seeds}"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
