package maxOnes

import FixedQeens.QueensPopulation
import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

class MaxOnes {
  static void main(String[] args) {
    int ones, populationPerNode, nodes
    double crossoverProbability, mutateProbability
    int instances, maxGenerations, replaceCount
    boolean maximise = true
    String fileName = ""
    List <Long> seeds = []
    if (args.size() != 0){
      ones = Integer.parseInt(args[0])
      nodes = Integer.parseInt(args[1])
      populationPerNode = Integer.parseInt(args[2])
      crossoverProbability = Double.parseDouble(args[3])
      mutateProbability = Double.parseDouble(args[4])
      replaceCount = Integer.parseInt(args[5])
      instances = Integer.parseInt(args[6])
      maxGenerations = Integer.parseInt(args[7])
      if (args.size() > 8){
        int seedCount = 0
        int i = 8
        while ( i < args.size()){
          seeds << Long.parseLong(args[i] )
          i++
          seedCount++
        }
        if (seedCount != nodes) {
          println "seedCount $seedCount does not equal nodes $nodes"
          System.exit(0)
        }
      }
    }
    else {
      println "program arguments MUST be specified"
      System.exit(0)
    }
    String outBase = "D:\\EAGAoutputs" + "/Ones${ones}/"
    def dir = new File(outBase)
    dir.mkdirs()
    String outName = outBase +
         "N${nodes}P${populationPerNode}" +
        "XP${crossoverProbability}MP${mutateProbability}RC${replaceCount}G${maxGenerations}.csv"
    println "Out File = $outName"

    def outFile = new File(outName)
    if (outFile.exists())outFile.delete()
    def outWriter = outFile.newPrintWriter()

    def eDetails = new DataDetails(dName: MaxOnePopulation.getName(),
        dInitMethod: MaxOnePopulation.initialiseMethod,
        dInitData: [instances],
        dCreateMethod: MaxOnePopulation.createInstance,
        dCreateData: [ones, populationPerNode,
                      nodes, maximise, crossoverProbability,
                      mutateProbability, null, fileName,
                      replaceCount])

    def rDetails = new ResultDetails(rName: MaxOneResult.getName(),
        rInitMethod: MaxOneResult.initialise,
        rInitData: [outWriter],
        rCollectMethod: MaxOneResult.collector,
        rFinaliseMethod: MaxOneResult.finalise)

    long startTime = System.currentTimeMillis()

    def chan1 = Channel.one2one()
    def chan2 = Channel.one2one()

    def emitter = new Emit(output: chan1.out(),
        eDetails: eDetails)

    def eaEngine = new EAGA_Engine<MaxOnePopulation>(input: chan1.in(),
        output: chan2.out(),
        printGeneration: false,
        generationLimit: maxGenerations,
        nodes: nodes)

    def collector = new Collect(input: chan2.in(),
        rDetails: rDetails)

    new PAR([emitter, eaEngine, collector]).run()

    long endTime = System.currentTimeMillis()

    println "MaxOnes Nodes $nodes " +
        "populationPerNode $populationPerNode " +
        "Ones $ones " +
        "crossover $crossoverProbability " +
        "mutate $mutateProbability " +
        "instances $instances " +
        "maxGeneration $maxGenerations " +
        "replace $replaceCount " +
        "Time ${endTime-startTime}"

    outWriter.println("$nodes, $populationPerNode, $ones, $crossoverProbability, "
        + "$mutateProbability, $maxGenerations, "
        + "$replaceCount, ${endTime-startTime}")

    outWriter.flush()
    outWriter.close()


  } // end of main
}
