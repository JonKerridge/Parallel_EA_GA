package queens

import parallel_ea_ga.IndividualInterface

class QueensIndividual implements IndividualInterface<QueensIndividual, QueensPopulation>{
  int N   // number of queens
  List <Integer> board = [] // board[0] is always null due to evaluateFitness function
  BigDecimal fitness

  QueensIndividual(int queens){
    this.N = queens
    fitness = 1000.0
  }

  void permute (Random rng) {
    board = []
    for ( int i in 1 .. N) board[i] = i
//        println "QC-permute: Client: $clientId board = $board"
    for (int i in 1 .. N) {
//            println "QC-permute: Client: $clientId i: $i"
      int j = rng.nextInt(N) + 1  //range is 1..N
      board.swap(i,j)
    }
  }

  @Override
  createIndividual(QueensPopulation population, Random rng) {
    permute(rng)
    evaluateFitness(population)
//    println "Initial Board: $board, Fit: $fitness"
//    println "Initial Fit: $fitness"
  }

  @Override
  def evaluateFitness(QueensPopulation population) {
    // population not used as no base data requirement
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

  @Override
  def mutate(Random rng) {
    int place1 = rng.nextInt(N) + 1  //1..queens
    int place2 = rng.nextInt(N) + 1
    while (place1 == place2) place2 = rng.nextInt(N) + 1
    board.swap(place1, place2)
  }

  @Override
  def prePoint(QueensIndividual other, int point) {
    return null
  }

  @Override
  def postPoint(QueensIndividual other, int point) {
    return null
  }

  @Override
  def midPoints(QueensIndividual other, int point1, int point2) {
    return null
  }
}
