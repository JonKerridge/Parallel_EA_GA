package maxOnes

import groovyParallelPatterns.DataClass

class MaxOnePopulation extends DataClass{

  //TODO insert the required individual type
  List <MaxOneIndividual> individuals = []  //
  boolean solutionFound

  int numberOfGenes       //length of an individual's chromosome
  int populationPerNode   // must be greater than 3
  int nodes
  int replaceCount        // number of generations before all children replaced

  List <Long> seeds = null
  boolean maximise = true             // implies looking for a maximum valued goal
  Double crossoverProbability = null   // probability of a crossover operation 0.0 ..< 1.0
  Double mutationProbability = null   // probability a child will be mutated 0.0 .. 1.0
  String fileName = ""                // some problems will need file input to create individuals
  static String initialiseMethod = "initialise"
  static String createInstance = "create"
  static String sortMethod = "quickSort"          // sorts into ascending order
  static String convergence = "convergence"
  static String crossover = "crossover"           // used to undertake the crossover operation
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
      // numberOfQueens, populationPerNode, nodes, fitPropertyName, maximise,
      // exactResult, convergenceValue, mutProbability, [seeds], fileName
      numberOfGenes = (int)d[0]
      populationPerNode = (int)d[1]
      nodes = (int)d[2]
      maximise = (boolean)d[3]
      crossoverProbability = (double)d[4]
      mutationProbability = (double)d[5]
      if (d[6] != null) {
        seeds = []
        d[6].each { seeds << (it as Long) }
      }
      else {
        seeds = []
        for ( i in 0 ..< nodes) seeds << null
      }
      fileName = d[7]
      replaceCount = d[8] as int

      assert populationPerNode >= 3: "Population: populationPerNode must be 3 or more not $populationPerNode"
      assert nodes >= 1: "Population: nodes ($nodes) must be >= 1"
      assert mutationProbability != null: "Population: mutationProbability must be specified"

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
      // initialise individuals to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
      // really would like to code
      // individuals << new I(params)  where I is the generic tyype
      //TODO make sure that an empty individual is returned
        individuals << new MaxOneIndividual(numberOfGenes, 0)
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

  boolean convergence(){
    // depends on the individuals and the fitness measure
    // this example is for a solution to the MaxOnes problems
    // where the fitness is best when all Genes are 1
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
    int xOverPoint = rng.nextInt(numberOfGenes)
    int geneLength = individuals[child1].geneLength
    for (i in 0 ..< xOverPoint)
      individuals[child1].genes[i] = individuals[best].genes[i]
    for (i in xOverPoint ..< geneLength)
      individuals[child1].genes[i] = individuals[secondBest].genes[i]
    for (i in 0 ..< xOverPoint)
      individuals[child2].genes[i] = individuals[secondBest].genes[i]
    for (i in xOverPoint ..< geneLength)
      individuals[child2].genes[i] = individuals[best].genes[i]
    return completedOK
  }

  int combineChildren(int parent1,
                      int parent2,
                      int worst1,
                      int worst2,
                      int child1,
                      int child2){
    // for example replace worst in individuals with best of child1 or child2
    // some versions could refer to best and secondBest
    if ( individuals[child1].getFitness() > individuals[child2].getFitness())
      individuals.swap(worst1, child1)
    else
      individuals.swap(worst1, child2)
    return completedOK
  }

  def copyParents(int parent1,
                  int parent2,
                  int child1,
                  int child2) {
    // copies the parents indicated into the child locations
    // needed when crossover is not done but there could be a subsequent mutation
    for ( i in 0 ..< numberOfGenes){
      individuals[child1].genes[i] = individuals[parent1].genes[i]
      individuals[child2].genes[i] = individuals[parent2].genes[i]
    }
  }

  // processes fileLines to create the problem specific data structures
  //TODO complete and add properties as necessary
  int processFile(){
    return -100
  }
}
