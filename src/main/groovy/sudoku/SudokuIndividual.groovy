package sudoku

import parallel_ea_ga.IndividualInterface

class SudokuIndividual implements IndividualInterface<SudokuIndividual, SudukoPopulation> {
  BigDecimal fitness = -1
  List nodesVisited = []
  List<List<Integer>> board
  int geneLength = 9  // modify this throughout once working

  SudukoPopulation population

  SudokuIndividual(int geneLength){
    this.geneLength = geneLength
  }

  @Override
  createIndividual(SudukoPopulation population, Random rng) {
    this.population = population
    board = []
    int row
    row = 0
    for (l in 0 ..< population.preBoard.size()) {
      List pb = population.preBoard[l]
      List<Integer> digits
      digits = (1..9).collect { it }
//      println "modifying $l = $pb"
      pb.each { d -> if (d != 0) digits = digits - [d] }
//      println "digits to place: $digits"
      List<Integer> revisedRow
      revisedRow = []
      for (i in 0..<population.fixed[row].size()) {
        if (population.fixed[row][i]) {
          if (digits.size() > 1) {
            Integer d = digits[rng.nextInt(digits.size())]
            revisedRow << d
            digits = digits - [d]
          } else revisedRow << digits[0]
        } else revisedRow << pb[i]
//        println "row $l: $revisedRow"
      }
      board << revisedRow.collect()
//      for ( e in 0..< 9) board[row][e] = revisedRow[e]
      row = row + 1
    }
    evaluateFitness(population)
//    String s ="\nCreated Board\n"
//    board.each {s = s + "$it" + "\n"}
//    s = s + "Created Fitness = $fitness\n"
//    println "$s"
  }

  def countDifferentDigits (List <Integer> values){
//    println "cDD: $rowCol"
    int differentDigits, digit
    differentDigits = 0
    digit = 1
    while (digit < 10){
      if (values.contains(digit)) {
        differentDigits += 1
      }
      digit += 1
    }
    return differentDigits
  }

  def createColumns(int column) {
    List <Integer> values
    values =[]
    for (row in 0..< 9) {
      values = values + board[row][column]
    }
    return values
  }

  def createBlocks ( int row, int column){
    List <Integer> values
    values =[]
    int br = row * 3
    int cr = column * 3
    for ( r in br ..< (br+3))
      for ( c in cr ..< (cr+3)){
        values = values + board[r][c]
      }
    return values
  }

  @Override
  evaluateFitness(SudukoPopulation population) {
     fitness = 0
    for ( r in 0 ..<3){
      for (c in 0 ..< 3){
        fitness = fitness + countDifferentDigits(createBlocks(r,c))
      }
    }
    for ( c in 0..< 9)
      fitness = fitness + countDifferentDigits(createColumns(c))
  }

  @Override
  BigDecimal getFitness() {
    return fitness
  }

  @Override
  def mutate(Random rng) {
    int row = rng.nextInt(9)
    List elements = []
    for ( i in 0 ..< 9) if (population.fixed[row][i]) elements << i
    int element1 = rng.nextInt(elements.size())
    int element2 = rng.nextInt(elements.size())
    while (element2 == element1) element2 = rng.nextInt(elements.size())
//    println "Before Board:"
//    board.each {println "$it"}
    board[row].swap(elements[element1], elements[element2])
//    println " $row[${elements[element1]}, ${elements[element2]}] "
//    println "After Mutate:"
//    board.each {println "$it"}
  }


  @Override
  prePoint(SudokuIndividual other, int point) {
    for ( i in 0 ..< point) board[i] = other.board[i].collect()
  }

  @Override
  postPoint(SudokuIndividual other, int point) {
    for ( i in point ..< 9) board[i] = other.board[i].collect()
  }

  @Override
  midPoints(SudokuIndividual other, int point1, int point2) {
  }

  String toString(){
    String s = "\nBoard\n"
    board.each(l ->  s = s + "$l" +"\n")
    return s
  }
}
