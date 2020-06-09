package FixedQeens


List <Integer> board = [null, 6, 3, 7, 1, 5, 8, 2, 4 ]  // [null, 6, 3, 1, 7, 5, 8, 2, 4 ]
//                        0   1  2  3  4  5  6  7  8

// check that no value is repeated
int queens = board.size() -1
int count = 0
for ( q in 1 .. queens)
  if (!board.contains(q)) count += 1
if (count != 0) println "Problem: $count rows are repeated"
else {
  println " No rows repeated"
  for (q1x in 1..<queens)
    for (q2x in q1x + 1..queens) {
      int q1y = board[q1x]
      int q2y = board[q2x]
      if (Math.abs(q1x - q2x) == Math.abs(q1y - q2y)) count += 1
  }
  if (count != 0) println "Problem: $count diagonal tests failed" else println " No diagonal failures"
}

int qc1 = 2
int qc2 = 4
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