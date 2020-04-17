package tsp

/*
taken from
https://en.wikipedia.org/wiki/Heap%27s_algorithm

procedure generate(k : integer, A : array of any):
  if k = 1 then
    output(A)
  else
    // Generate permutations with kth unaltered
    // Initially k == length(A)
    generate(k - 1, A)
    // Generate permutations for kth swapped with each k-1 initial
    for i := 0; i < k-1; i += 1 do
    // Swap choice dependent on parity of k (even or odd)
      if k is even then
         swap(A[i], A[k-1]) // zero-indexed, the kth is at k-1
      else
        swap(A[0], A[k-1])
      end if
      generate(k - 1, A)
    end for
  end if
*/

distances = [
    [0],
    [0, 0, 8, 39, 37, 50, 61],
    [0, 8, 0, 45, 47, 49, 62],
    [0, 39, 45, 0, 9, 21, 21],
    [0, 37, 47, 9, 0, 15, 20],
    [0, 50, 49, 21, 15, 0, 17],
    [0, 61, 62, 21, 20, 17, 0]
]

List <Integer> minRoute
minFit = 10000


int evaluateFitness (List<Integer> route){
  int distance
  distance = 0
  for ( int i in 1 .. 6){
    distance = distance + distances[route[i-1]] [route[i]]
  }
  return distance
//  println "$distance"
//  if ( distance < ){
//    minRoute = route
//    minFit = distance
//  }
}

count = 1

location = []
def processA(List <Integer> a){
  List <Integer> route = [1] + a + [1]
  int fit = evaluateFitness(route)
  println "$count:  \t$route = $fit"
  if ( fit < minFit){
     minFit = fit
    location =[]
  }
  if (fit == minFit) location << count
  count += 1
}

def generate (int k, List<Integer> a){
  if (k == 1) {
    processA(a)
  }
  else {
    generate(k-1, a)
    for ( i in 0 ..< k-1){
      if ((k % 2) == 0)
        a.swap(i, k-1)
      else
        a.swap(0, k-1)
      generate(k-1, a)
    }
  }
}

a = [2,3,4,5,6]
generate(5,a)
println " Minfit = $minFit at $location"
