package FixedQeens

List <Integer> board = [null, 3, 1, 4, 2, 50, 44, 36, 30, 17, 23, 48, 28, 43, 64, 38, 57, 32, 39, 29, 40, 9, 13, 5, 42, 37, 19, 52, 47, 53, 56, 62, 35, 6, 10, 63, 15, 21, 25, 46, 51, 31, 59, 26, 33, 49, 8, 61, 22, 12, 14, 60, 58, 34, 20, 16, 27, 11, 7, 45, 54, 41, 18, 55, 24]
//List <Integer> board = [null, 6, 3, 7, 1, 5, 8, 2, 4 ]  // [null, 6, 3, 1, 7, 5, 8, 2, 4 ]
//                        0   1  2  3  4  5  6  7  8

// check that no value is repeated
int queens = board.size() -1
int count = 0
for ( q in 1 .. queens)
  if (!board.contains(q)) count += 1
if (count != 0) println "Problem: $count rows are repeated"
else {
  println " No rows repeated for $queens queens"
  for (q1x in 1..<queens)
    for (q2x in q1x + 1..queens) {
      int q1y = board[q1x]
      int q2y = board[q2x]
      if (Math.abs(q1x - q2x) == Math.abs(q1y - q2y)) count += 1
  }
  if (count != 0)
    println "Problem: $count diagonal tests failed"
  else
    println " No diagonal failures"
}

int qc1 = 1
int qc2 = queens
int qr1, qr2, qn
boolean stillOK = true
while ( stillOK && (qc1 <= qc2)) {
  qn = qc1 + 1
  while ( stillOK && (qn <= qc2)) {
    qr1 = board[qc1]
    qr2 = board[qn]
    if (Math.abs(qc1 - qn) == Math.abs(qr1 - qr2))
      stillOK = false
    else
      qn++
  }
  qc1++
}
println "$stillOK: ${qc1-1}, ${qn-1}"