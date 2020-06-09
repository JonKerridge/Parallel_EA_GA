package sudoku

import parallel_ea_ga.IndividualInterface

class SudokuIndividual implements IndividualInterface<SudokuIndividual, SudukoPopulation> {
  BigDecimal fitness = -1
  List<List<Integer>> board
  int geneLength = 9  // modify this throughout once working
  boolean specificMutation
  SudukoPopulation population

  SudokuIndividual(int geneLength){
    this.geneLength = geneLength
  }

  @Override
  createIndividual(SudukoPopulation population, Random rng) {
    this.population = population
    specificMutation = false
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

  BigDecimal getBlockFitness(){
    BigDecimal blockFitness = 0
    for ( r in 0 ..<3){
      for (c in 0 ..< 3){
        blockFitness = blockFitness + countDifferentDigits(createBlocks(r,c))
      }
    }
    return blockFitness
  }

  List <Integer> findWrongColumns(){
    List<Integer> wrongColumns = []
    for ( c in 0 ..< 9)
      if (countDifferentDigits(createColumns(c)) != 9) wrongColumns << c
    return wrongColumns
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
    if (!specificMutation) {
      int row = rng.nextInt(9)
      List elements = []
      for (i in 0..<9) if (population.fixed[row][i]) elements << i
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
  }

  def processColumns ( List<Integer> columns,
                       List <Integer> testColumns,
                       int parent,
                       int child,
                       Random rng){
    for ( b in 0 ..< population.numberOfGenes)
      for ( i in 0 ..< population.numberOfGenes)
        population.individuals[child].board[b][i] = population.individuals[parent].board[b][i]
    population.individuals[child].specificMutation = true
    if (columns.intersect(testColumns) != []){
      // first three columns have a problem
      int row
      row = rng.nextInt(9)
      List <Integer> elements
      elements = []
      testColumns.each { i -> if (population.fixed[row][i]) elements << i }
      while (elements.size() < 2){
        row = (row + 1) % 9
        elements = []
        testColumns.each { i -> if (population.fixed[row][i]) elements << i }
      }
      // have a row with at least variable entries in block
      if (elements.size() == 2) // only two elements so use them
        population.individuals[child].board[row].swap(elements[0], elements[1])
      else{
        int element1 = rng.nextInt(3)
        int element2 = rng.nextInt(3)
        while (element2 == element1) element2 = rng.nextInt(elements.size())
        population.individuals[child].board[row].swap(elements[element1], elements[element2])
      } // inner elements if
    } // end of outer intersection if

  } // end of process columns

  def specificMutate(int parent,
                     int child,
                     List <Integer> columns,
                     Random rng){
//    println "SM [$parent, $child] at $columns\n" +
//      "${population.individuals[parent].toString()}"
    processColumns(columns,[0,1,2], parent, child, rng)
    processColumns(columns,[3,4,5], parent, child, rng)
    processColumns(columns,[6,7,8], parent, child, rng)
  }

  String toString(){
    String s = "Board $fitness\n"
    board.each(l ->  s = s + "$l" +"\n")
    return s
  }
}
