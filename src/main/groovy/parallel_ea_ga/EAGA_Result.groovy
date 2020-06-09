package parallel_ea_ga

import groovyParallelPatterns.DataClass

class EAGA_Result extends DataClass{

  static String initialise = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"
  static int resultNumber

  int initClass ( List d){
    resultNumber = 1
    return completedOK
  }

  //TODO change type of input Object to individual
  int collect(Object data){
    // insert collect processing as required
    resultNumber += 1
    return completedOK
  }


  int finalise(List d){
    return completedOK
  }

}
