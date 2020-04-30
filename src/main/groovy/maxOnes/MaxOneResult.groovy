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

  //TODO change type of input Object to individual individuals
  int collector(MaxOnePopulation data){
    BigDecimal fitness
    fitness = data.individuals[data.first].getFitness()
    println "Result: $resultNumber = Time: ${data.timeTaken}; Fitness = $fitness; " +
          "Generations: ${data.generations}\n " +
          "Seeds: ${data.seeds}"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
