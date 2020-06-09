package tsp

String nodeFile = "./10cities.csv"

List < List<Integer> > nodeData = []
nodeData [0] = [0, 0, 0]

new File(nodeFile).eachLine {String line ->
  println "$line"
  List <String> values = line.tokenize(',')
  println "$values"
  List <Integer> nodeValues = []
  values.each{v-> nodeValues << Integer.parseInt(v)}
  nodeData << nodeValues
}
println "NodeData \n"
nodeData.each { println "$it"}

int nodes = nodeData.size()
List < List<Integer> > triangular = []

for ( i in 0 ..< nodes) triangular[i] = []
triangular[0].putAt(0, 0)
triangular[1].putAt(0, 0)
triangular[1].putAt(1, 0)
for ( i in 2..< nodes) {
  for ( j in 1 ..< i){
    int side1, side2
    side1 = Math.abs(nodeData[i][1] - nodeData[j][1])
    side2 = Math.abs(nodeData[i][2] - nodeData[j][2])
    double dLength = Math.sqrt((side1 * side1) + (side2 * side2))
    triangular[i].putAt(j, Math.round(dLength + 0.49D))
  }
  triangular[i].putAt(i, 0)
  triangular[i].putAt(0, 0)
}

String outFileName = "./10cities.tsp"
def outFile = new File(outFileName)
if (outFile.exists()) outFile.delete()
def printWriter = outFile.newPrintWriter()

println "Lower Triangular Version \n"
triangular.each {println "$it"}
println "Actual Lower Triangular data set\n"
for ( i in 1 ..< nodes) {
  for (j in 1..i) {
    print "${triangular[i][j]} "
    printWriter.print("${triangular[i][j]},")
  }
  println()
  printWriter.println()
}
printWriter.flush()
printWriter.close()


