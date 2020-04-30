package parallel_ea_ga

interface IndividualInterfaceRecord<T1, T2> {
  // T1 is the individual class
  // T2 is the population of individuals class a modified copy of EAGA_Population
  // the methods assume that an individual has the desired internal structure
  //
  // BigDecimal fitness
  // int size of representation ie number of genes
  // Collection individualRepresentation
  // List or Map of nodesVisited
  //
  // requires a public constructor

  updateNodesVisited(int nodeId)

  createIndividual(T2 population, Random rng)     // creates an individual as this.

  evaluateFitness(T2 population)   // evaluates the fitness function for an individual

  BigDecimal getFitness()    // returns the individual's fitness used in combine Children
                      // called when printing data about each generation in Root

  // undertakes a mutation operation on an individual
  mutate(Random rng)

}