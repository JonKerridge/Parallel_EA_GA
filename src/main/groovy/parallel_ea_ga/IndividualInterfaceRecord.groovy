package parallel_ea_ga

interface IndividualInterfaceRecord<T1, T2> {
  // T1 is the individual class
  // T2 is the population class
  // the methods assume that an individual has the desired internal structure

  updateNodesVisited(int nodeId)

  createIndividual(T2 population, Random rng)     // creates a member of the population

  evaluateFitness(T2 population)   // evaluates the fitness function for an individual

  BigDecimal getFitness()    // returns the individual's fitness used in combine Children
                      // called when printing data about each generation in Root

  // undertakes a mutation operation on an individual
  mutate(Random rng)

  // the following implement crossover;
  // in combination 1 and 2-point crossovers can be undertaken

  // crossover calling and other from 0 ..< point
  prePoint(T1 other, int point)

  // crossover calling and other from point .. end
  postPoint(T1 other, int point)

  // crossover calling and other from point1 ..< point2
  midPoints(T1 other, int point1, int point2)

}