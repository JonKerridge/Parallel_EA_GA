package PartialQeens


List <Integer> board = [null, 3, 1, 4, 2, 36, 22, 29, 21, 38, 53, 37, 33, 58, 51, 34, 9, 45, 25, 60, 11, 18, 12, 63, 49, 42, 35, 32, 52, 61, 57, 43, 6, 13, 17, 20, 56, 7, 44, 31, 55, 27, 15, 54, 47, 24, 10, 41, 62, 5, 8, 26, 14, 19, 30, 50, 40, 28, 46, 16, 23, 48, 39, 59, 64]

// check that no value is repeated
int queens = board.size() -1
int count = 0
for ( q in 1 .. queens)
  if (!board.contains(q)) count += 1
if (count != 0) println "Problem: $count rows are repeated"
else {
  println " No rows repeated"
  for (q1x in 1..<queens) for (q2x in q1x + 1..queens) {
    int q1y = board[q1x]
    int q2y = board[q2x]
    if (Math.abs(q1x - q2x) == Math.abs(q1y - q2y)) count += 1
  }
  if (count != 0) println "Problem: $count diagonal tests failed" else println " No diagonal failures"
}
