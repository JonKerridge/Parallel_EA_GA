package FixedQeens

def queenRun = new QueensMain()

int folder = 7
List queens = [32]
List fixedQueens = [0,4,6,8]
List nodePop = [[16, 4], [12, 5], [8, 8]]
List crossoverPoints = [8]
List probabilities =[[1.0, 1.0], [1.0, 0.8], [1.0, 0.6], [1.0, 0.4], [1.0, 0.2],
                     [0.75, 1.0], [0.75, 0.8], [0.75, 0.6], [0.75, 0.4], [0.75, 0.2],
                     [0.5, 1.0], [0.5, 0.8], [0.5, 0.6], [0.5, 0.4], [0.5, 0.2] ]
//                     [0.25, 1.0], [0.25, 0.8], [0.25, 0.6], [0.25, 0.4] ]
List replaceCount = [1000]
int instances = 10
int maxGen = 25000
List seeds = [11L,23L,31L,43L,53L,61L,71L,83L,97L,101L,113L,127L,131L,149L,151L,163L]

queens.each{q ->
  fixedQueens.each {f ->
    nodePop.each {np ->
      crossoverPoints.each {cp ->
        probabilities.each{p ->
          replaceCount.each{rc ->
            queenRun.runQueens(q, f, np[0], np[1], cp, p[0], p[1], rc, instances, maxGen, folder, seeds)
          }
        }
      }
    }
  }
}


