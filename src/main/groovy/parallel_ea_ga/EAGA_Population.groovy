package parallel_ea_ga

import groovy.transform.CompileStatic
import groovy_parallel_patterns.DataClass

//@CompileStatic
class EAGA_Population extends DataClass{

  //TODO insert the required individual type
  List individuals = []  //  the list of individuals that form the individuals
  boolean solutionFound
  List fileLines = [] // used to store inout file if used

  int numberOfGenes       //length of an individual's chromosome
  int populationPerNode   // must be greater than 3
  int nodes               // number of node processes
  int replaceCount        // number of generations before new individuals created and
                          // possibly added to the population
  List <Long> seeds = null
  boolean maximise = true             // implies looking for a maximum valued goal
  Double crossoverProbability = null   // probability of a crossover operation 0.0 ..< 1.0
  Double mutationProbability = null   // probability a child will be mutated 0.0 ..< 1.0
  String fileName = ""                // some problems will need file input to create individuals
  static String processFile = "processFile"      // used to read the individual creation
            // may require the addition of further data fields in the individuals
  // unless otherwise stated these methods return completedOK unless otherwise indicated
  static String initialiseMethod = "initialise"     // used to initialise the number of generated instances
  static String createInstance = "create"           // creates each instance object but not the individuals
                                                    // returns normalCompletion or normalTermination
  static String sortMethod = "quickSort"            // sorts individuals into ascending order called in Root
  static String convergence = "convergence"         // determines if there is convergence to a solution
                                                    // returns a boolean true if converged false otherwise
                                                    // called in Root process
  static String crossover = "crossover"             // used to undertake the crossover operation
                                                    // called from a Node process
  static String combineChildren = "combineChildren" // called after crossover and mutation
                                                    // to combine one or both children into individuals
  static String copyParentsToChildren = "copyParents" //used to copy parent individuals to children
  // when crossovers not undertaken prior to mutation

  int first, last             // index of first and last entry in individuals, depends on maximise
  int lastIndex               // subscript of last entry in individuals,regardless
  static int instance
  static int instances
  long timeTaken

  int generations = 0


  int initialise(List d){
    instances = d[0]
    instance = 0
    return completedOK
  }

  int create(List d){
    if ( instance == instances) return normalTermination
    else {
      // numberOfQueens, populationPerNode, nodes, maximise,
      // crossoverProbability, mutationProbability, [seeds], fileName
      numberOfGenes = (int)d[0]
      populationPerNode = (int)d[1]
      nodes = (int)d[2]
      maximise = (boolean)d[3]
      crossoverProbability = (double)d[4]
      mutationProbability = (double)d[5]
      seeds = []
      if (d[6] != null)
        d[6].each { seeds << (it as Long) }
      else
        // seeds will be generated in the node
        for ( i in 0 ..< nodes) seeds << null

      fileName = d[7]
      replaceCount = (int) d[8]
      // todo some codes may need further property initialisations

      assert populationPerNode >= 4: "Population: populationPerNode must be 4 or more not $populationPerNode"
      assert nodes >= 1: "Population: nodes ($nodes) must be >= 1"
      assert mutationProbability != null: "Population: mutationProbability must be specified"
      assert crossoverProbability != null: "Population: crossoverProbability must be specified"

      // set values of first and last index in individuals, depends on maximise
      lastIndex = (nodes * populationPerNode) - 1
      if (maximise) {
        first = lastIndex
        last = 0
      } else {
        first = 0
        last = lastIndex
      }
      instance = instance + 1
      generations = 0
      individuals = []
      // initialise individuals to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
      // really would like to code
      // individuals << new I(params)  where I is the generic type
      //TODO make sure that an empty individual is returned
        individuals << null  // MUST be changed
      return normalContinuation
    }
  }

  int quickSort( boolean sortType){
    // always sorts into ascending order
    if (sortType)
    // just include the active population
      quickSortRun ( individuals, 0, lastIndex)
    else
    // used after child replacement, so include children
      quickSortRun(individuals, 0, lastIndex +(nodes*2))
    return  completedOK
  }

  int partition(List m, int start, int end){
    BigDecimal pivotValue
    pivotValue = m[start].getFitness()
    // getFitness returns a BigDecimal
//    println "P1: $start, $end, $pivotValue"
    int left, right
    left = start+1
    right = end
    boolean done
    done = false
    while (!done){
//      println "P2: $left, $right $pivotValue"
      while ((left <= right) && (m[left].getFitness() < pivotValue)) left = left + 1
//      println "P3: $left, $right, $pivotValue, ${m[left].getProperty(fitPropertyName)}"
      while ((m[right].getFitness() >= pivotValue) && (right >= left)) right = right - 1
//      println "P4: $left, $right, $pivotValue, $pivotValue, ${m[right].getProperty(fitPropertyName)}"
      if (right < left)
        done = true
      else {
        m.swap(left, right)
//        println "swap $left with $right for $pivotValue"
      }
    }
    m.swap(start, right)
    return right
  }

  void quickSortRun(List b, int start, int end){
//    println "QSR1: $start, $end"
    if (start < end) {
      int splitPoint = partition(b, start, end)
//      println "QSR2: $start, $end, $splitPoint"
      quickSortRun(b, start, splitPoint-1)
      quickSortRun(b, splitPoint+1, end)
    }
  }

  //TODO modify the convergence criterion
  boolean convergence(){
    // depends on the individuals and the fitness measure
    // this example is for a solution to the MaxOnes problems
    // where the fitness is best when all Genes are 1
    // getFitness returns a BigDecimal
    //todo this will need to be changed
    return (individuals[first].getFitness()  == numberOfGenes)
  }

  // must be modified for each application
  // crossover undertakes a crossover operation on two members of the individuals
  // rng is used to create one or more cross over points
  // assuming ONE crossover point splitting an individual into before and after the point
  // individuals[child1] = individuals[best].before plus individuals[secondBest]after
  // individuals[child2] = individuals[secondBest].before plus individuals[best]after
  int crossover(int best,
                int secondBest,
                int child1,
                int child2,
                Random rng) {
    // todo write code required to undertake crossover of parents to children
    println "crossover method -  not implemented"
    System.exit(0)
    return completedOK
  }

  int combineChildren(int parent1,
                      int parent2,
                      int worst1,
                      int worst2,
                      int child1,
                      int child2){
    // for example replace worst in individuals with best of child1 or child2
    // some versions could refer to replacing worst2
    // todo write code required to children
    println "combineChildren method -  not implemented"
    System.exit(0)
    return completedOK
  }

  def copyParents(int parent1,
                  int parent2,
                  int child1,
                  int child2) {
    // copies the parents indicated into the child locations
    // needed when crossover is not done but there could be a subsequent mutation
    // todo write code required to copy parents to children
    println "copyParentsToChildren method - copyParents not implemented"
    System.exit(0)
  }

    // processes fileLines to create the problem specific data structures
  //TODO complete and add properties as necessary
  int processFile(){
    println "processFile method - not implemented"
    System.exit(0)
    return -100
  }

}
