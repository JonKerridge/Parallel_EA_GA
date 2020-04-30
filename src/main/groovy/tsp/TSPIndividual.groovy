package tsp

import parallel_ea_ga.IndividualInterface

class TSPIndividual implements IndividualInterface<TSPIndividual, TSPPopulation>{
  List <Integer> route = []
  BigDecimal distance = 0
  int cities = 0

  TSPIndividual(int cities){
    this.cities = cities
  }

  @Override
  createIndividual(TSPPopulation population, Random rng) {
    route[0] = 1          // start and end at city 1
    route[cities] = 1
    int place = rng.nextInt(cities) + 1    // 1 .. cities
    for ( i in 1 ..< cities ) {
      while ((place == 1) || (route.contains(place))) place = rng.nextInt(cities) + 1
      route[i] = place
    }
    evaluateFitness(population)
//    println "Individual = $route Fitness = $distance"
  }

  @Override
  evaluateFitness(TSPPopulation population) {
    distance = 0
    for ( int i in 1 .. cities){
      distance = distance + population.distances[route[i-1]] [route[i]]
    }
  }

  @Override
   BigDecimal getFitness() {
    return distance
  }

  @Override
  mutate(Random rng) {
    int place1 = rng.nextInt(cities - 2) + 1  //1..cities-1
    int place2 = rng.nextInt(cities - 2) + 1
    while (place1 == place2) place2 = rng.nextInt(cities - 1) + 1
    route.swap(place1, place2)
  }

}
