package queens

import groovyParallelPatterns.DataClass

class QueensPopulation extends DataClass{

  //TODO insert the required individual type
  List <QueensIndividual> population = []  //
  boolean solutionFound
  List fileLines = [] // used to store inout file if used

  int numberOfGenes       //length of an individual's chromosome
  int populationPerNode   // must be greater than 3
  int nodes
  List <Long> seeds = null
  boolean maximise = true             // implies looking for a maximum valued goal
  Double crossoverProbability = null   // probability of a crossover operation 0.0 ..< 1.0
  Double mutationProbability = null   // probability a child will be mutated 0.0 ..< 1.0
  String fileName = ""                // some problems will need file input to create individuals
  static String processFile = "processFile"      // used to read the individual creation
            // may require the addition of further data fields in the population
  // unless otherwise stated these methods return completedOK unless otherwise indicated
  static String initialiseMethod = "initialise"     // used to initialise the number of generated instances
  static String createInstance = "create"           // creates each instance object but not the individuals
                                                    // returns normalCompletion or normalTermination
  static String sortMethod = "quickSort"            // sorts population into ascending order called in Root
  static String convergence = "convergence"         // determines if there is convergence to a solution
                                                    // returns a boolean true if converged false otherwise
                                                    // called in Root process
  static String crossover = "queensCrossover"             // used to undertake the crossover operation
                                                    // called from a Node process
  static String combineChildren = "combineChildren" // called after crossover and mutation
                                                    // to combine one or both children into population

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
      // numberOfGenes, populationPerNode, nodes, maximise,
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
        population << new QueensIndividual(numberOfGenes) // MUST be changed
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
//  BigDecimal minFitness = 100.0
//  int minCount
//  List <Integer> minBoard

  boolean convergence(){
//    BigDecimal currentFitness = population[first].getFitness()
//    if (currentFitness < minFitness){
//      minCount = 0
//      minFitness = currentFitness
//      minBoard = []
//      for (i in 1..< population[first].board.size())
//        minBoard << population[first].board[i]  // omit initial board null
//    }
//    minCount += 1
//    if (minCount > 1000){
//      println " Best Board: $minBoard, Fitness: $minFitness"
//      return true
//    }
//    else return false
    return (population[first].getFitness() == 0.0)
  }

  def doCrossover (List sB, List mB, List mSb, List eB, int child){

    // find common values in mB and mSB and remove from mB
    // leaves value in mB that have to be inserted into child1
    int mSize = mSb.size()
//    println "X0: $sB\n$mB\n$eB\n$mSb"
    for ( i in 0 ..< mSize) {
      int v = mSb[i]
      int j = 0
      boolean notFound = true
      while ( (notFound) && (j < mSize)) {
        if ( v == mB[j]) {
          notFound = false
          mB.remove(j)   // removes the jth element
        }
        else
          j = j + 1
      }
    }   // end of for  mB contains non-common elements
//    println "X1: $sB\n$mB\n$eB\n$mSb"
    // now iterate through mSb looking for matches in sB
    // replace any with values from those remaining in mB
    for ( i in 0..< mSize) {
      if (sB.contains(mSb[i])) {
        int v = mSb[i]
        int j = 0
        boolean notFound = true
        while  (notFound) {
          if (v == sB[j]) {
            notFound = false
            sB[j] = mB.pop()
          }
          else
            j = j + 1
        }
      }
    } // end of for
//    println "X2: $sB\n$mB\n$eB\n$mSb"
    // mow iterate through mSb for matches in eB
    // and replace any with remaining values from mB

    for ( i in 0..< mSize) {
      if (eB.contains(mSb[i])) {
        int v = mSb[i]
        int j = 0
        boolean notFound = true
        while  (notFound) {
          if (v == eB[j]) {
            notFound = false
            eB[j] = mB.pop()
          }
          else
            j = j + 1
        }
      }
    } // end for mB should now be empty
// board[0] is always null
    population[child].board = [null] +sB + mSb + eB as List<Integer>
//    println "X3: $sB\n$mB\n$eB\n$mSb\n${population[child].board}"
  } // end of doCrossover

  static List <Integer> extractParts(int start, int end, List<Integer> source){
    List<Integer> result = []
    for ( i in start ..< end) result << source[i]
    return result
  }

  int queensCrossover(int best,
                int secondBest,
                int worst,
                int child1,
                int child2,
                Random rng) {
    // must ensure crossover points are not 0 as board[0] is not used
    int c1 = rng.nextInt(numberOfGenes-3) + 2
    int c2 = rng.nextInt(numberOfGenes-2) + 1
    //ensure c1 and c2 are different
    while ( c1 == c2) c2 = rng.nextInt(numberOfGenes-2) + 1
    if (c1 > c2) (c1,c2)=[c2,c1]  // ensure c1 < c2
    // for child1 NB route[0] and route[N] are fixed as 1
    //child1  1 ..< c1  = best 1..<c1
    //child1 c1 ..< c2   = secondBest c1 ..< c2
    //child1 c2 ..< N = best c2 ..< N     where N is number of cities
    // s = start, m = middle, e = end of B best or sB secondBest

    List <Integer> sB = extractParts(1, c1, population[best].board)
    List <Integer> mB = extractParts(c1, c2, population[best].board)
    List <Integer> mSb = extractParts(c1, c2, population[secondBest].board)
    List <Integer> eB = extractParts(c2, numberOfGenes+1, population[best].board)
    doCrossover(sB, mB, mSb, eB, child1)

    // now do it the other way round
//    sB = population[secondBest].route.getAt(1 ..< c1)
//    mB = population[secondBest].route.getAt(c1 ..< c2)
//    mSb = population[best].route.getAt(c1 ..< c2)
//    eB = population[secondBest].route.getAt(c2 ..< numberOfGenes)
    sB = extractParts(1, c1, population[secondBest].board)
    mB = extractParts(c1, c2, population[secondBest].board)
    mSb = extractParts(c1, c2, population[best].board)
    eB = extractParts(c2, numberOfGenes+1, population[secondBest].board)
    doCrossover(sB, mB, mSb, eB, child2)
    return completedOK
  }

  int combineChildren(int best,
                      int secondBest,
                      int worst,
                      int child1,
                      int child2){
    // for example replace worst in population with best of child1 or child2
    // some versions could refer to best and secondBest
    if ( population[child1].getFitness() < population[child2].getFitness())
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
