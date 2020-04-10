package parallel_ea_ga

import groovyParallelPatterns.DataClass

class EAGA_Population extends DataClass{

  //TODO insert the required individual type
  List  population = []  //
  boolean solutionFound

  List fileLines = [] // used to store inout file if used

  int numberOfGenes       //length of an individual's chromosome
  int populationPerNode   // must be greater than 3
  int nodes
  List <Long> seeds = null
  boolean maximise = true             // implies looking for a maximum valued goal
  Double mutationProbability = null   // probability a child will be mutated 0.0 .. 1.0
  String fileName = ""                // some problems will need file input to create individuals
  static String processFile = "processFile"      // used to read the individual creation
            // may require the addition of further data fields in the population
  // unless otherwise stated these methods return completedOK unless otherwise indicated
  static String initialiseMethod = "initialise"   // used to initialise the number of generated instances
  static String createInstance = "create"         // creates each instance object but not the individuals
                                                  // returns normalCompletion or normalTermination
  static String sortMethod = "quickSort"          // sorts population into ascending order called in Root
  static String convergence = "convergence"       // determines if there is convergence to a solution
                                                  // returns a boolean true if converged false otherwise
                                                  // called in Root process
  static String crossover = "crossover"           // used to undertake the crossover operation
                                                  // called from a Node process

  int first, last             // index of first and last entry in population, depends on maximise
  int lastIndex               // subscript of last entry in population,regardless
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
      // numberOfGenes, populationPerNode, nodes, fitPropertyName, maximise,
      // exactResult, convergenceValue, mutProbability, [seeds], fileName
      numberOfGenes = (int)d[0]
      populationPerNode = (int)d[1]
      nodes = (int)d[2]
      maximise = (boolean)d[3]
      mutationProbability = (double)d[4]
      if (seeds != null) {
        seeds = []
        d[5].each { seeds << (it as Long) }
      }
      fileName = d[6]

      assert populationPerNode >= 3: "Population: populationPerNode must be 3 or more not $populationPerNode"
      assert nodes >= 1: "Population: nodes ($nodes) must be >= 1"
      assert mutationProbability != null: "Population: mutationProbability must be specified"

      // set values of first and last index in population, depends on maximise
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
      population = []
      // initialise population to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
      // really would like to code
      // population << new I(params)  where I is the generic type
      //TODO make sure that an empty individual is returned
        population << null  // MUST be changed
      return normalContinuation
    }
  }

  int quickSort( ){
    // always sorts into ascending order
    quickSortRun ( population, 0, lastIndex)
    return  completedOK
  }

  int partition(List m, int start, int end){
    def pivotValue
    pivotValue = m[start].getFitness()
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
    // depends on the population and the fitness measure
    // this example is for a solution to the MaxOnes problems
    // where the fitness is best when all Genes are 1
    return (population[first].getFitness()  == numberOfGenes)
  }

  // must be modified for each application
  // crossover undertakes a crossover operation on two members of the population
  // rng is used to create one or more cross over points
  // assuming ONE crossover point splitting an individual into before and after the point
  // population[child1] = population[best].before plus population[secondBest]after
  // population[child2] = population[secondBest].before plus population[best]after
  // a mutation is undertaken on both children provided a randomly generated value is less than
  // mutateProbability
  // the fitness of the two children are then evaluated
  // whichever child has the best fitness is then swapped with population[worst]
  int crossover(int best,
                int secondBest,
                int worst,
                int child1,
                int child2,
                double mutateProbability,
                Random rng){
    int xOverPoint = rng.nextInt(numberOfGenes)
    population[child1].prePoint(population[best], xOverPoint )
    population[child2].prePoint(population[secondBest], xOverPoint )
    population[child2].postPoint(population[best], xOverPoint )
    population[child1].postPoint(population[secondBest], xOverPoint )

    // now see if we do a mutation
    if ( rng.nextDouble() < mutateProbability) population[child1].mutate(rng)
    if ( rng.nextDouble() < mutateProbability) population[child2].mutate(rng)

    // evaluate the child fitness values
    population[child1].evaluateFitness()
    population[child2].evaluateFitness()

    // now replace worst in population with best of child1 or child2
    if ( population[child1].getFitness() > population[child2].getFitness())
      population.swap(worst, child1)
    else
      population.swap(worst, child2)
    return completedOK
  }

  // processes fileLines to create the problem specific data structures
  //TODO complete and add properties as necessary
  int processFile(){
    return -100
  }

}
