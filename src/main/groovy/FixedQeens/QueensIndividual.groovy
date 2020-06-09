package FixedQeens

import parallel_ea_ga.IndividualInterface

class QueensIndividual implements IndividualInterface<QueensIndividual, FixedQeens.QueensPopulation>{
  int N   // number of queens
  List <Integer> board = [] // board[0] is always null due to evaluateFitness function
  BigDecimal fitness
  int fixedQueens = 0
  int firstMovableQueen
//  int mutateRepeats  // not used in this version

  QueensIndividual(int queens, int fixedQueens){
    this.N = queens
    fitness = 1000.0
    this.fixedQueens = fixedQueens
    firstMovableQueen = fixedQueens + 1
  }

  void permute (Random rng) {
    for ( int i in firstMovableQueen .. N) board[i] = i
//        println "QC-permute: Client: $clientId board = $board"
    for (int i in firstMovableQueen .. N) {
//            println "QC-permute: Client: $clientId i: $i"
      int j = rng.nextInt(N - fixedQueens)  + firstMovableQueen  //range is firstMovableQueen..N
      board.swap(i,j)
    }
  }

  @Override
  createIndividual(FixedQeens.QueensPopulation population, Random rng) {
    board = []
//    mutateRepeats = population.mutateRepeats // not used in mutate at moment
    // place the fixed queens
    for ( i in 0 .. fixedQueens) board[i] = population.fixedLocations[i]
    permute(rng)
    evaluateFitness(population)
//    println "Initial Board: $board, Fit: $fitness"
//    println "Initial Fit: $fitness"-*-
  }

  @Override
  def evaluateFitness(FixedQeens.QueensPopulation population) {
    // population not used as no repeated base data requirement
    List <Integer> leftDiagonal = []
    List <Integer> rightDiagonal = []
    double sum = 0.0D

    for ( i in 1 .. 2*N) {
      leftDiagonal[i] = 0
      rightDiagonal[i] = 0
    }
    for ( i in 1 .. N) {
      int idxL = i+board[i]-1
      leftDiagonal[idxL]++
      int idxR = N-i+board[i]
      rightDiagonal[idxR]++
//            rightDiagonal[N-i+board[i]]++
    }
    for ( i in 1 .. ((2*N) - 1)) {
      int counter = 0
      if ( leftDiagonal[i] > 1)
        counter += leftDiagonal[i] - 1
      if ( rightDiagonal[i] > 1)
        counter += rightDiagonal[i] - 1
      sum += counter / (N - Math.abs(i-N))
    }
    // target fitness is 0.0
    // sum can be negative so return absolute value
    fitness = Math.abs(sum)
  }

  @Override
  BigDecimal getFitness() {
    // fitness is a double so we need to ensure that we return a sensible value
    return fitness
  }

//  @Override
//  def mutate(Random rng) {
//    // must only swap movable queens
//    int repeats = rng.nextInt(mutateRepeats)+1
//    for ( r in 1 .. repeats) {
//      int place1 = rng.nextInt(N - fixedQueens) + firstMovableQueen  // firstMovableQueen..N
//      int place2 = rng.nextInt(N - fixedQueens) + firstMovableQueen
//      while (place1 == place2) place2 = rng.nextInt(N - fixedQueens) + firstMovableQueen
//      board.swap(place1, place2)
//    }
//  }


  @Override
  def mutate(Random rng) {
    // must only swap movable queens
    int n1 = rng.nextInt(N - fixedQueens)  + firstMovableQueen
    int n2 = rng.nextInt(N - fixedQueens)  + firstMovableQueen
    while (n2 == n1) n2 = rng.nextInt(N - fixedQueens) + firstMovableQueen
    board.swap(n1, n2)
  }

}
