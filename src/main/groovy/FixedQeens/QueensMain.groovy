package FixedQeens

import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

class QueensMain {
  static void main(String[] args) {
    int queens
    int fixedQueens
    int nodes
    int populationPerNode
    int crossoverPoints
    double crossoverProbability
    double mutateProbability
    int replaceCount
    int instances
    int maxGenerations
    List <Long> seeds = []
    println "Program Arguments\n$args"
    if (args.size() != 0){
      queens = Integer.parseInt(args[0])
      fixedQueens = Integer.parseInt(args[1])
      nodes = Integer.parseInt(args[2])
      populationPerNode = Integer.parseInt(args[3])
      crossoverProbability = Double.parseDouble(args[4])
      mutateProbability = Double.parseDouble(args[5])
      replaceCount = Integer.parseInt(args[6])
      instances = Integer.parseInt(args[7])
      maxGenerations = Integer.parseInt(args[8])
      crossoverPoints = Integer.parseInt(args[9])
      if (args.size() > 10){
        int seedCount = 0
        int i = 10
        while ( i < args.size()){
          seeds << Long.parseLong(args[i] )
          i++
          seedCount++
        }
      }
    }
    else {
      println "program arguments MUST be specified"
      System.exit(0)
    }

    String userDir
    userDir = "D:\\IJGradle\\EA_GA\\src\\main\\groovy\\FixedQeens"
    boolean maximise = false
    String fileName = "$userDir" + "/Fixed$fixedQueens-1.txt"
    String outBase = "D:\\EAGAoutputs\\MXPQueens3"  + "/Queens${queens}Fixed${fixedQueens}/"
    def dir = new File(outBase)
    dir.mkdirs()
    String outName = outBase +
        "N${nodes}P${populationPerNode}M${crossoverPoints}" +
        "XP${crossoverProbability}MP${mutateProbability}RC${replaceCount}" +
        "G${maxGenerations}.csv"
    println "In File = $fileName\nOut File = $outName"

    def outFile = new File(outName)
    if (outFile.exists())outFile.delete()
    def outWriter = outFile.newPrintWriter()

    def eDetails = new DataDetails(
        dName: QueensPopulation.getName(),
        dInitMethod: QueensPopulation.initialiseMethod,
        dInitData: [instances],
        dCreateMethod: QueensPopulation.createInstance,
        dCreateData: [queens, populationPerNode,
                      nodes, maximise, crossoverProbability,
                      mutateProbability, null, fileName,
                      replaceCount, fixedQueens, crossoverPoints ]
    )
    def rDetails = new ResultDetails (
        rName: QueensResult.getName(),
        rInitMethod: QueensResult.initialise,
        rInitData: [outWriter],
        rCollectMethod: QueensResult.collector,
        rFinaliseMethod: QueensResult.finalise
    )

    long startTime = System.currentTimeMillis()

    def chan1 = Channel.one2one()
    def chan2 = Channel.one2one()

    def emitter = new Emit( output: chan1.out(),
        eDetails: eDetails )

    def eaEngine = new EAGA_Engine<QueensPopulation>(
        input: chan1.in(),
        output: chan2.out(),
        printGeneration: false,
        generationLimit: maxGenerations,
        nodes: nodes
    )

    def collector = new Collect( input: chan2.in(),
        rDetails: rDetails)

    new PAR([emitter, eaEngine, collector]).run()

    long endTime = System.currentTimeMillis()

    println "Queens Nodes $nodes " +
        "Population $populationPerNode " +
        " Queens $queens " +
        "Fixed $fixedQueens " +
        "Crossover $crossoverProbability " +
        "Mutate $mutateProbability " +
        "Instances $instances " +
        "MaxGeneration $maxGenerations " +
        "Method-Points $crossoverPoints " +
        "Replace $replaceCount " +
        "Time ${endTime-startTime}"

    outWriter.println("$nodes, $populationPerNode, $queens, $fixedQueens, $crossoverProbability, "
        + "$mutateProbability, $maxGenerations, "
        + "$crossoverPoints, $replaceCount, ${endTime-startTime}")

    outWriter.flush()
    outWriter.close()

  }

  public runQueens (
      int queens,
      int fixedQueens,
      int nodes,
      int populationPerNode,
      int crossoverPoints,
      double crossoverProbability,
      double mutateProbability,
      int replaceCount,
      int instances,
      int maxGenerations,
      int folderNumber,
      List <Long> seeds = [11L,23L,31L,43L,53L,61L,71L,83L,97L,101L,113L,127L,131L,149L,151L,163L] ) {
    String userDir
    userDir = "D:\\IJGradle\\EA_GA\\src\\main\\groovy\\FixedQeens"
    boolean maximise = false
    String fileName = "$userDir" + "/Fixed$fixedQueens-1.txt"
    String outBase = "D:\\EAGAoutputs\\MXPQueens${folderNumber}" + "/Queens${queens}Fixed${fixedQueens}/"
    def dir = new File(outBase)
    dir.mkdirs()
    String outName = outBase + "N${nodes}" +
        "P${populationPerNode}" +
        "CP${crossoverPoints}" +
        "XP${crossoverProbability}" +
        "MP${mutateProbability}" +
        "RC${replaceCount}" +
        "G${maxGenerations}.csv"
    println "In File = $fileName\nOut File = $outName"

    def outFile = new File(outName)
    if (outFile.exists()) {
      println "file already exists"
      println "Queens Nodes $nodes " +
          "Population $populationPerNode " +
          " Queens $queens " +
          "Fixed $fixedQueens " +
          "Crossover $crossoverProbability " +
          "Mutate $mutateProbability " +
          "Instances $instances " +
          "MaxGeneration $maxGenerations " +
          "Method-Points $crossoverPoints " +
          "Replace $replaceCount "
//      outFile.delete()
    } else {
      def outWriter = outFile.newPrintWriter()

      def eDetails = new DataDetails(dName: QueensPopulation.getName(),
          dInitMethod: QueensPopulation.initialiseMethod,
          dInitData: [instances],
          dCreateMethod: QueensPopulation.createInstance,
          dCreateData: [queens, populationPerNode,
                        nodes, maximise, crossoverProbability,
                        mutateProbability, seeds, fileName,
                        replaceCount, fixedQueens, crossoverPoints])
      def rDetails = new ResultDetails(rName: QueensResult.getName(),
          rInitMethod: QueensResult.initialise,
          rInitData: [outWriter],
          rCollectMethod: QueensResult.collector,
          rFinaliseMethod: QueensResult.finalise)

      long startTime = System.currentTimeMillis()

      def chan1 = Channel.one2one()
      def chan2 = Channel.one2one()

      def emitter = new Emit(output: chan1.out(),
          eDetails: eDetails)

      def eaEngine = new EAGA_Engine<QueensPopulation>(input: chan1.in(),
          output: chan2.out(),
          printGeneration: false,
          generationLimit: maxGenerations,
          nodes: nodes)

      def collector = new Collect(input: chan2.in(),
          rDetails: rDetails)

      new PAR([emitter, eaEngine, collector]).run()

      long endTime = System.currentTimeMillis()

      println "Queens Nodes $nodes " +
          "Population $populationPerNode " +
          " Queens $queens " +
          "Fixed $fixedQueens " +
          "Crossover $crossoverProbability " +
          "Mutate $mutateProbability " +
          "Instances $instances " +
          "MaxGeneration $maxGenerations " +
          "Method-Points $crossoverPoints " +
          "Replace $replaceCount " +
          "Time ${endTime - startTime}"

      outWriter.println("$nodes, $populationPerNode, $queens, $fixedQueens, $crossoverProbability, " + "$mutateProbability, $maxGenerations, " + "$crossoverPoints, $replaceCount, ${endTime - startTime}")

      outWriter.flush()
      outWriter.close()
    }
  }
}
