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

  //TODO change type of input Object to individual individuals
  int collector(SudukoPopulation data){
    List<List<Integer>> solution = []
    solution = data.individuals[data.first].board.collect()
    String s = "\nSolution:\n"
    solution.each(l ->  s = s + "$l" +"\n")
    println "Result: $resultNumber = Solution Found is  ${data.solutionFound}; " +
        "Time: ${data.timeTaken}; " +
        "Generations: ${data.generations}; " +
        "$s " +
        "Fitness is ${data.individuals[data.first].getFitness()}\n"+
        "Seeds: ${data.seeds}\n"
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
