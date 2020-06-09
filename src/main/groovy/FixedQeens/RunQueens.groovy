package FixedQeens

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine
int queens = 256
int fixedQueens = 0

int nodes = 16
int populationPerNode = 4

double crossoverProbability = 0.5
double mutateProbability = 0.8
int replaceCount = 1000

List <Long> seeds = []

if (args.size() != 0){
  queens = Integer.parseInt(args[0])
  fixedQueens = Integer.parseInt(args[1])
  nodes = Integer.parseInt(args[2])
  populationPerNode = Integer.parseInt(args[3])
  crossoverProbability = Double.parseDouble(args[4])
  mutateProbability = Double.parseDouble(args[5])
  mutateRepeats = Integer.parseInt(args[6])
  replaceCount = Integer.parseInt(args[7])
}

int instances = 10
int maxGenerations = 100000

String userDir
userDir = "D:\\IJGradle\\EA_GA\\src\\main\\groovy\\FixedQeens"
boolean maximise = false
String fileName = "$userDir" + "/Fixed$fixedQueens-1.txt"
String outBase = "$userDir" + "/csvFixed${queens}F${fixedQueens}/"
def dir = new File(outBase)
dir.mkdirs()
String outName = outBase +
                "Q${queens}F${fixedQueens}N${nodes}P${populationPerNode}" +
                "XP${crossoverProbability}MP${mutateProbability}RC${replaceCount}X2.csv"
println "In File = $fileName\nOut File = $outName"

def outFile = new File(outName)
if (outFile.exists())outFile.delete()
def outWriter = outFile.newPrintWriter()

def eDetails = new DataDetails(
    dName: FixedQeens.QueensPopulation.getName(),
    dInitMethod: FixedQeens.QueensPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: FixedQeens.QueensPopulation.createInstance,
    dCreateData: [queens, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, null, fileName,
                  replaceCount, fixedQueens ]
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
    "Method ${FixedQeens.QueensPopulation.crossover} " +
    "Replace $replaceCount " +
    "Time ${endTime-startTime}"

outWriter.println("$nodes, $populationPerNode, $queens, $fixedQueens, $crossoverProbability, "
+ "$mutateProbability, $maxGenerations, "
    + "${FixedQeens.QueensPopulation.crossover}, $replaceCount, ${endTime-startTime}")

outWriter.flush()
outWriter.close()
