package FixedQeens

import groovy_parallel_patterns.DataClass

class QueensPopulation extends DataClass{

  //TODO insert the required individual type
  List <QueensIndividual> individuals = []  //
  boolean solutionFound
  List fileLines = [] // used to store inout file if used

  int numberOfQueens
  int populationPerNode   // must be greater than 4
  int nodes
  int fixedQueens         // indicates the number of queens that have a fixed position
  int firstMovableQueen   // index of first queen that can be moved
  int replaceCount        // number of generations before all children replaced
  int points              // number of points in crossover must be even 2,4,6,8 ....

  List <Integer> fixedLocations = []  // holds indication of fixed position queens
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
//  static String crossover = "queens2PointCrossover" // used to undertake the crossover operation
                                                    // called from a Node process
//  static String crossover = "queens4PointCrossover"
  static String crossover = "multiPointCrossover"
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
      numberOfQueens = (int)d[0]
      populationPerNode = (int)d[1]
      nodes = (int)d[2]
      maximise = (boolean)d[3]
      crossoverProbability = (double)d[4]
      mutationProbability = (double)d[5]
      seeds = []
      if (d[6] != null)
        d[6].each { seeds << (it + instance as Long) }
      else
        // seeds will be generated in the node
        for ( i in 0 ..< nodes) seeds << null
      fileName = d[7]
      replaceCount = d[8] as int
      fixedQueens = d[9] as int
      points = d[10] as int


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
      firstMovableQueen = fixedQueens + 1
      instance = instance + 1
      generations = 0
      individuals = []
      // initialise individuals to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
      // really would like to code
      // individuals << new I(params)  where I is the generic type
      //TODO make sure that an empty individual is returned
        individuals << new QueensIndividual(numberOfQueens, fixedQueens)
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
    return (individuals[first].getFitness() == 0.0)
  }


//  def doCrossover (List sB, List mB, List mSb, List eB, int child){
//
//    // find common values in mB and mSB and remove from mB
//    // leaves value in mB that have to be inserted into child1
//    int mSize = mSb.size()
////    println "X0: $sB\n$mB\n$eB\n$mSb"
//    for ( i in 0 ..< mSize) {
//      int v = mSb[i]
//      int j = 0
//      boolean notFound = true
//      while ( (notFound) && (j < mSize)) {
//        if ( v == mB[j]) {
//          notFound = false
//          mB.remove(j)   // removes the jth element
//        }
//        else
//          j = j + 1
//      }
//    }   // end of for  mB contains non-common elements
////    println "X1: $sB\n$mB\n$eB\n$mSb"
//    // now iterate through mSb looking for matches in sB
//    // replace any with values from those remaining in mB
//    for ( i in 0..< mSize) {
//      if (sB.contains(mSb[i])) {
//        int v = mSb[i]
//        int j = 0
//        boolean notFound = true
//        while  (notFound) {
//          if (v == sB[j]) {
//            notFound = false
//            sB[j] = mB.pop()
//          }
//          else
//            j = j + 1
//        }
//      }
//    } // end of for
////    println "X2: $sB\n$mB\n$eB\n$mSb"
//    // mow iterate through mSb for matches in eB
//    // and replace any with remaining values from mB
//
//    for ( i in 0..< mSize) {
//      if (eB.contains(mSb[i])) {
//        int v = mSb[i]
//        int j = 0
//        boolean notFound = true
//        while  (notFound) {
//          if (v == eB[j]) {
//            notFound = false
//            eB[j] = mB.pop()
//          }
//          else
//            j = j + 1
//        }
//      }
//    } // end for mB should now be empty
//// board[0] is always null
//    individuals[child].board = [null] +sB + mSb + eB as List<Integer>
////    println "X3: $sB\n$mB\n$eB\n$mSb\n${individuals[child].board}"
//  } // end of doCrossover

//  static List <Integer> extractParts(int start, int end, List<Integer> source){
//    List<Integer> result = []
//    for ( i in start ..< end) result << source[i]
//    return result
//  }

  static def FindSubscriptsOfBinA(List <Integer> a, List <Integer> b){
    // returns the subscripts in a where elements of b can be found
//    print "\t\tFind $b in $a -> returns "
    List <Integer> subscripts = []
    for (  i in 0 ..< a.size())
      if ( b.contains(a[i])) subscripts << i
//    print "$subscripts"
    return subscripts
  }

  static def extractParts(int start, int end, List source){
    // copies source[start ]..< source[end] into result
    List<Integer> result = []
    for ( i in start ..< end) result << source[i]
    return result
  }

  int partCheck (int start, int end, int id){
    // determines how much of the individual[id]'s board is valid
    // in the range start ..< end
    // returns the last value up to which the sequence is valid
    boolean stillOK = true
    int qc1, qr1, qr2, qn
    qc1 = start
    while ( stillOK && (qc1 <= end)) {
      qn = qc1 + 1
      while ( stillOK && (qn <= end)) {
        qr1 = individuals[id].board[qc1]
        qr2 = individuals[id].board[qn]
        if (Math.abs(qc1 - qn) == Math.abs(qr1 - qr2))
          stillOK = false
        else
          qn++
      }
      qc1++
    }
   return (qn-1)
  }

  def copyParents(int parent1,
                  int parent2,
                  int child1,
                  int child2) {
    // copies the parents indicated into the child locations
    // needed when crossover is not done but there could be a subsequent mutation
    individuals[child1].board = extractParts(0, numberOfQueens+1, individuals[parent1].board)
    individuals[child2].board = extractParts(0, numberOfQueens+1, individuals[parent2].board)
  }
// TODO
  int multiPointCrossover (
      int parent1,
      int parent2,
      int child1,
      int child2,
      Random rng ){
    List <Integer> randoms = [1]  // first queen is in location 1 of board
    for (n in 1 .. points ){
      int c = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
      while ( randoms.contains(c)) c = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
      randoms << c
    }
    randoms << numberOfQueens + 1
    randoms = randoms.sort()
//    println "\n\n\nParent1 $parent1 = ${individuals[parent1].board}"
//    println "Parent2 $parent2 = ${individuals[parent2].board}"
//    println "Randoms = $randoms"
    // randoms contains a sorted list of random points between the first movable queen and the
    // end of the board
    List <List <Integer>> partsOf1 = []   // all the parts of first parent
    for ( i in 0 .. points){
      partsOf1[i] = extractParts(randoms[i] as int, randoms[i+1] as int, individuals[parent1].board)
    }
    List <List <Integer>> partsOf2 = []   // odd parts of second parent
    // crossover is between the odd subsections of partsOf1 and each subsection
    // of partsOf2 in turn
    int section = 1
    while (section < points) {
      partsOf2 << extractParts(randoms[section] , randoms[section+1], individuals[parent2].board)
      section = section + 2
    }
    doMultiPointCrossover(partsOf1, partsOf2, child1)

    // now do it the other way round between the parents and to a different child
    partsOf1 = []
    partsOf2 = []
    for ( i in 0 .. points){
      partsOf1[i] = extractParts(randoms[i] as int, randoms[i+1] as int, individuals[parent2].board)
    }
    section = 1
    while (section < points) {
      partsOf2 << extractParts(randoms[section] as int, randoms[section+1] as int, individuals[parent1].board)
      section = section + 2   // we take the odd sections for processing
    }
    doMultiPointCrossover(partsOf1, partsOf2, child2)
    return completedOK
  }

  def doMultiPointCrossover( List <List <Integer>>  partsOf1,
                             List <List <Integer>>  partsOf2,
                             int child ){
    /*
    the number of crossover Points is even
    The number of subsections in partsOf1 is 2 * points + 1
    there are points sections in partsOf2
    p1Values holds the sum of the odd subsection of partsOf1
    p2Values holds the sum of the even sections of partsOf2
    The even numbered subsections of partsOf1 will be those
    that are involved in the crossover operation
     */
    List <Integer> p1Values, p2Values, reallocate, common, searchSet, subscripts
    p1Values = []
    p2Values = []
    int bitOf1, bitOf2
    bitOf1 = 1
    bitOf2 = 0
//    println "\n\ndoMPC\n parts1= $partsOf1\nparts2 = $partsOf2\n"
    while (bitOf1 < points) {
      p1Values = p1Values + partsOf1[bitOf1]
      bitOf1 = bitOf1 + 2
    }
    while ( bitOf2 < partsOf2.size()) {
      p2Values = p2Values + partsOf2[bitOf2]
      bitOf2++
    }
    /*
    reallocate is the value that need to be reassigned in the result
    common is those value common to both p1Values and p2Values
    searchSet are those values that need to be replaced by
    members of reallocate.
    each even subsection of partsOf1 is searched to find the subscripts of any elements in both sets
    any values found in the subsection of partsO1 can be replaced by taking a value from reallocate
     */
    reallocate = p1Values - p2Values
    common = p1Values.intersect(p2Values)
    searchSet = p2Values - common
    assert reallocate.size() == searchSet.size(): "Set sizes in dMPC not equal"
    bitOf1 = 0
    while ( bitOf1 < partsOf1.size()) {
      subscripts = FindSubscriptsOfBinA(partsOf1[bitOf1], searchSet)
      subscripts.each{s -> partsOf1[bitOf1][s] = reallocate.pop()}
      bitOf1 = bitOf1 + 2
    }
    /* now rebuild the replacement child by appending the now modified even subsections of partsOf1
    and the unaltered subsections of partsOf2 in sequence.
    The parts are appended to a null value as the zeroth element of a board is always null
    The final updated version of the individual's board is obtained by flatten()ing
     */
    individuals[child].board = [null]
    bitOf1 = 0
    bitOf2 = 0
    while ( bitOf1 < points) {
      individuals[child].board << partsOf1[bitOf1]
      individuals[child].board << partsOf2[bitOf2]
      bitOf1 = bitOf1 + 2
      bitOf2++
    }
    individuals[child].board << partsOf1[points]
    individuals[child].board = individuals[child].board.flatten()
//    println "\nChild $child = ${individuals[child].board}"
  }

//  def do2PointCrossover(List <Integer> p1a, List <Integer> p1b,
//                        List <Integer> p1c, List <Integer> p2b, int child){
//    List <Integer> commonB = p1b.intersect(p2b)
//    List <Integer> remainP1B = p1b.minus(commonB)
//    List <Integer> remainP2B = p2b.minus(commonB)
//    List <Integer> commonA = p1a.intersect(remainP2B)
//    List <Integer> subscripts = FindSubscriptsOfBinA(p1a, commonA)
//    subscripts.each {s -> p1a[s] = remainP1B.pop()}
//    subscripts = FindSubscriptsOfBinA(p1c, remainP2B)
//    subscripts.each {s -> p1c[s] = remainP1B.pop()}
//    individuals[child].board = [null] + p1a + p2b + p1c as List <Integer>
//  }
//
//  int queens2PointCrossover (int parent1,
//                             int parent2,
//                             int child1,
//                             int child2,
//                             Random rng){
//    // must ensure crossover points are not 0 as board[0] is not used
//    int c1 = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
//    int c2 = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
//    //ensure c1 and c2 are different
//    while ( c1 == c2) c2 = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
//    if (c1 > c2) (c1,c2)=[c2,c1]  // ensure c1 < c2
//    // extract the parts of the parents; parts are labelled: a,b,c
////    int c2r = partCheck(c1, c2, parent2)
//    int c2r = c2
//    List <Integer> p1a = extractParts(1, c1, individuals[parent1].board)
//    List <Integer> p1b = extractParts(c1, c2r, individuals[parent1].board)
//    List <Integer> p2b = extractParts(c1, c2r, individuals[parent2].board)
//    List <Integer> p1c = extractParts(c2r, numberOfQueens+1, individuals[parent1].board)
//    do2PointCrossover(p1a, p1b, p1c, p2b, child1)
//    // now the other child
////    c2 = partCheck(c1, c2, parent1)
//    List <Integer> p2a = extractParts(1, c1, individuals[parent2].board)
//    p2b = extractParts(c1, c2, individuals[parent2].board)
//    p1b = extractParts(c1, c2, individuals[parent1].board)
//    List <Integer> p2c = extractParts(c2, numberOfQueens+1, individuals[parent2].board)
//    do2PointCrossover(p2a, p2b, p2c, p1b, child2)
//    return completedOK
//  }
//
//  def do4PointCrossover(List <Integer> p1a, List <Integer> p1b, List <Integer> p1c,
//                        List <Integer> p1d, List <Integer> p1e, List <Integer> p2b,
//                        List <Integer> p2d, int child)  {
//    List <Integer> commonB = p1b.intersect(p2b)
//    List <Integer> remainP1B = p1b.minus(commonB)
//    List <Integer> remainP2B = p2b.minus(commonB)
//
//    List <Integer> commonA = p1a.intersect(remainP2B)
//    List <Integer> subscripts = FindSubscriptsOfBinA(p1a, commonA)
//    subscripts.each {s -> p1a[s] = remainP1B.pop()}
//
//    List <Integer> commonC = p1c.intersect(remainP2B)
//    subscripts = FindSubscriptsOfBinA(p1c, commonC)
//    subscripts.each {s -> p1c[s] = remainP1B.pop()}
//
//    List <Integer> commonD = p1d.intersect(remainP2B)
//    subscripts = FindSubscriptsOfBinA(p1d, commonD)
//    subscripts.each {s -> p1d[s] = remainP1B.pop()}
//
//    List <Integer> commonE = p1e.intersect(remainP2B)
//    subscripts = FindSubscriptsOfBinA(p1e, commonE)
//    subscripts.each {s -> p1e[s] = remainP1B.pop()}
//
//    commonD = p1d.intersect(p2d)
//    List <Integer> remainP1D = p1d.minus(commonD)
//    List <Integer> remainP2D = p2d.minus(commonD)
//
//    commonA = p1a.intersect(remainP2D)
//    subscripts = FindSubscriptsOfBinA(p1a, commonA)
//    subscripts.each {s -> p1a[s] = remainP1D.pop()}
//
//    commonC = p1c.intersect(remainP2D)
//    subscripts = FindSubscriptsOfBinA(p1c, commonC)
//    subscripts.each {s -> p1c[s] = remainP1D.pop()}
//
//    commonE = p1e.intersect(remainP2D)
//    subscripts = FindSubscriptsOfBinA(p1e, commonE)
//    subscripts.each {s -> p1e[s] = remainP1D.pop()}
//
//    individuals[child].board = [null] + p1a + p2b + p1c + p2d + p1e
//  }
//
//  int queens4PointCrossover (int parent1,
//                             int parent2,
//                             int child1,
//                             int child2,
//                             Random rng) {
//    List randoms = []
//    List <Integer> p1a, p1b, p1c, p1d, p1e, p2b, p2d
//    for (n in 1 .. 4 ){
//      int c = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
//      while ( randoms.contains(c)) c = rng.nextInt(numberOfQueens - fixedQueens) + firstMovableQueen
//      randoms << c
//    }
//    randoms = randoms.sort()
//    int n1 = randoms[0] as int
//    int n2 = randoms[1] as int
//    int n3 = randoms[2] as int
//    int n4 = randoms[3] as int
//    // randoms in sorted order now extract the five + 2 parts
//    p1a = extractParts(1, n1, individuals[parent1].board)
//    p1b = extractParts(n1, n2, individuals[parent1].board)
//    p1c = extractParts(n2, n3, individuals[parent1].board)
//    p1d = extractParts(n3, n4, individuals[parent1].board)
//    p1e = extractParts(n4, individuals[parent1].board.size(), individuals[parent1].board)
//    p2b = extractParts(n1, n2, individuals[parent2].board)
//    p2d = extractParts(n3, n4, individuals[parent2].board)
//    do4PointCrossover(p1a, p1b, p1c, p1d, p1e, p2b, p2d, child1)
//    // now the other way round for child2
//    p1a = extractParts(1, n1, individuals[parent2].board)
//    p1b = extractParts(n1, n2, individuals[parent2].board)
//    p1c = extractParts(n2, n3, individuals[parent2].board)
//    p1d = extractParts(n3, n4, individuals[parent2].board)
//    p1e = extractParts(n4, individuals[parent2].board.size(), individuals[parent2].board)
//    p2b = extractParts(n1, n2, individuals[parent1].board)
//    p2d = extractParts(n3, n4, individuals[parent1].board)
//    do4PointCrossover(p1a, p1b, p1c, p1d, p1e, p2b, p2d, child2)
//    return completedOK
//  }

//  int combineChildren(int parent1,
//                      int parent2,
//                      int worst1,
//                      int worst2,
//                      int child1,
//                      int child2){
//    // for example replace worst in individuals with best of child1 or child2
//    // some versions could refer to best and secondBest
//    if ( individuals[child1].getFitness() < individuals[child2].getFitness())
//      individuals.swap(worst1, child1)
//    else
//      individuals.swap(worst1, child2)
//    return completedOK
//  }

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
    fixedLocations = [null] // zero'th element is null
    // only one line of numbers
    fileLines.each { String line ->
      List <String> values = line.tokenize(',')
      for ( v in 0 ..< values.size())
        fixedLocations << Integer.parseInt(values[v])
    }
    return completedOK
  }

}
