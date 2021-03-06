package maxOnes

import parallel_ea_ga.IndividualInterface

class MaxOneIndividual implements IndividualInterface <MaxOneIndividual, MaxOnePopulation>{
  int geneLength
  BigDecimal fitness = -1
  int [] genes = new int[geneLength]

  MaxOneIndividual(int geneLength, int fitness) {
    this.geneLength = geneLength
    this.fitness = fitness
  }

  @Override
  createIndividual(MaxOnePopulation population, Random rng) {
    // fileName not required for individual generation
    fitness = 0
    for ( i in 0 ..< geneLength)
      genes[i] = rng.nextInt(2)
  }

  @Override
  evaluateFitness(MaxOnePopulation population) {
    fitness = 0
    for ( i in 0 ..< geneLength) fitness = fitness + genes[i]
  }

  BigDecimal getFitness(){
    return fitness
  }

  @Override
  mutate(Random rng) {
    int mutationPoint = rng.nextInt(geneLength)
    genes[mutationPoint] = 1 - genes[mutationPoint]
  }

}
