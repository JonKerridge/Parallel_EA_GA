package sudoku

import groovyParallelPatterns.DataClass

class SudokuResult extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual population
  int collector(SudukoPopulation data){
    List<List<Integer>> solution = []
    solution = data.population[data.first].board.collect()
    String s = "\nSolution : size is ${solution.size()}\n"
    solution.each(l ->  s = s + "$l" +"\n")
    println "Result: $resultNumber = Solution Found is  ${data.solutionFound}; " +
        "Time: ${data.timeTaken}; " +
        "Generations: ${data.generations}; " +
        "$s " +
        "Fitness is ${data.population[data.first].getFitness()}\n"+
        "Seeds: ${data.seeds}\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
