package tsp

import groovyParallelPatterns.DataClass

class TSPPopulation extends DataClass{

  //TODO insert the required individual type
  List  <TSPIndividual> individuals = []  //
  boolean solutionFound
  List fileLines = [] // used to store inout file if used

  List<List<Integer>> distances = []

  int numberOfGenes       //length of an individual's chromosome
  int populationPerNode   // must be greater than 3
  int nodes
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
  static String crossover = "crossoverDantzig42"    // used to undertake the crossover operation
                                                    // called from a Node process
  static String combineChildren = "combineChildren" // called after crossover and mutation
                                                    // to combine one or both children into individuals

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
      individuals = []
      // initialise individuals to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
      // really would like to code
      // individuals << new I(params)  where I is the generic type
      //TODO make sure that an empty individual is returned
        individuals << new TSPIndividual(numberOfGenes)  // MUST be changed
      return normalContinuation
    }
  }

  int quickSort( ){
    // always sorts into ascending order
    quickSortRun ( individuals, 0, lastIndex)
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

  //TODO modify the convergence criterion
  BigDecimal minFitness = 1000000
  int minCount
  List<Integer> minRoute
  boolean convergence(){
    BigDecimal currentFitness = individuals[first].getFitness()
    if (currentFitness < minFitness) {
      minFitness = currentFitness
      minCount = 0
      minRoute = []
      for ( i in 0 ..< individuals[first].route.size())
        minRoute << individuals[first].route[i]
    }
    minCount += 1
    if (minFitness > 800) return false
//    println"Convergence: $minFitness -> $first:= ${individuals[first].getFitness()}; $last:= ${individuals[last].getFitness()}"
    if ( minCount > 1000){
      println "Solution = Fitness: $minFitness, Minimum Route: $minRoute "
      return true
    } else return false
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
    // now construct the final route inserting city 1 to start and end
    individuals[child].route = [1] + sB + mSb + eB + [1]
//    println "X3: $sB\n$mB\n$eB\n$mSb\n${individuals[child].route}"
  } // end of doCrossover

  List <Integer> extractParts(int start, int end, List<Integer> source){
    List<Integer> result = []
    for ( i in start ..< end) result << source[i]
    return result
  }

  int crossoverDantzig42 (int parent1,
                          int parent2,
                          int child1,
                          int child2,
                          Random rng){
    // must ensure crossover points are not 0 and 42, which are both fixed at 1
    int c1 = rng.nextInt(numberOfGenes - 1) + 1
    int c2 = rng.nextInt(numberOfGenes - 1) + 1
    //ensure c1 and c2 are different
    while ( c1 == c2) c2 = rng.nextInt(numberOfGenes - 1) + 1
    if (c1 > c2) (c1,c2)=[c2,c1]  // ensure c1 < c2
    // for child1 NB route[0] and route[N] are fixed as 1
    //child1  1 ..< c1  = best 1..<c1
    //child1 c1 ..< c2   = secondBest c1 ..< c2
    //child1 c2 ..< N = best c2 ..< N     where N is number of cities
    // s = start, m = middle, e = end of B best or sB secondBest

    List <Integer> sB = extractParts(1, c1, individuals[parent1].route)
    List <Integer> mB = extractParts(c1, c2, individuals[parent1].route)
    List <Integer> mSb = extractParts(c1, c2, individuals[parent2].route)
    List <Integer> eB = extractParts(c2, numberOfGenes, individuals[parent1].route)
    doCrossover(sB, mB, mSb, eB, child1)

    // now do it the other way round
//    sB = individuals[secondBest].route.getAt(1 ..< c1)
//    mB = individuals[secondBest].route.getAt(c1 ..< c2)
//    mSb = individuals[best].route.getAt(c1 ..< c2)
//    eB = individuals[secondBest].route.getAt(c2 ..< numberOfQueens)
    sB = extractParts(1, c1, individuals[parent2].route)
    mB = extractParts(c1, c2, individuals[parent2].route)
    mSb = extractParts(c1, c2, individuals[parent1].route)
    eB = extractParts(c2, numberOfGenes, individuals[parent2].route)
    doCrossover(sB, mB, mSb, eB, child2)
    return completedOK
  }

  int combineChildren(int parent1,
                      int parent2,
                      int worst1,
                      int worst2,
                      int child1,
                      int child2) {
    BigDecimal child1Fit, child2Fit, worst2Fit
    child1Fit = individuals[child1].getFitness()
    child2Fit = individuals[child2].getFitness()
    worst2Fit = individuals[worst2].getFitness()
    // for example replace worst in individuals with best of child1 or child2
    // some versions could refer to best and secondBest
    if (child1Fit < child2Fit)
      if (child2Fit < worst2Fit) {
//        println"overwriting both: w < c1, 2w < c2"
        individuals.swap(worst1, child1)
        individuals.swap(worst2, child2)
      } else {
        individuals.swap(worst1, child1)
//        println "overwritng w < c1"
      }
    else
      if (child1Fit < worst2Fit) {
        individuals.swap(worst1, child2)
        individuals.swap(worst2, child1)
//        println"overwriting both: w < c2, 2w < c1"
      } else {
        individuals.swap(worst1, child2)
//        println "overwritng w < c2"
      }
    return completedOK
  }

  // processes fileLines to create the problem specific data structures
  //TODO complete and add properties as necessary
  int processFile(){
    distances[0] = [0]
    int row
    row = 1
    // process data lines into lower triangular
    fileLines.each {String line ->
      distances[row]= [0]
      List <String> values = line.tokenize(',')
      for ( v in 0 ..< values.size()) distances[row][v+1] = Integer.parseInt(values[v])
      row += 1
    }
    int rows = distances.size()
    for ( r in 1 ..< rows)
      for ( rc in r+1 ..< rows)
        distances[r][rc] = distances[rc][r]

//    println "\nSquare\n"
//    distances.each{println "$it"}

    return completedOK
  }

}
