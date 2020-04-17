package tsp

String inFile = "./dantzig42.tsp"
List < List <Integer> > distances = []
distances[0] = [0]
int row
row = 1

new File(inFile).eachLine {line ->
  distances[row]= [0]
  List <String> values = line.tokenize(',')
  for ( v in 0 ..< values.size()) distances[row][v+1] = Integer.parseInt(values[v])
  row += 1
}
//println "Triangular\n"
//distances.each{println "$it"}

int rows = 7
for ( r in 1 ..< rows)
  for ( rc in r+1 ..< rows)
    distances[r][rc] = distances[rc][r]

println "\nSquare\n"
//distances.each{println "$it"}
for ( r in 0 ..< rows)
  println "${distances[r]}"