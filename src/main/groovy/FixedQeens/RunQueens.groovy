package FixedQeens

import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int queens = 32
int fixedQueens = 6
int nodes = 16
int populationPerNode = 4
int crossoverPoints = 2
double crossoverProbability = 0.75
double mutateProbability = 0.8
int replaceCount = 500
int instances = 10
int maxGenerations = 25000
List <Long> seeds = [11L,23L,31L,43L,53L,61L,71L,83L,97L,101L,113L,127L,131L,149L,151L,163L]


String userDir
userDir = "D:\\IJGradle\\EA_GA\\src\\main\\groovy\\FixedQeens"
boolean maximise = false
String fileName = "$userDir" + "/Fixed$fixedQueens-1.txt"
String outBase = "$userDir" + "/csvFixed${queens}F${fixedQueens}V1/"
def dir = new File(outBase)
dir.mkdirs()
String outName = outBase +
                "Q${queens}F${fixedQueens}N${nodes}P${populationPerNode}" +
                "XP${crossoverProbability}MP${mutateProbability}G${maxGenerations}" +
                "XOP${crossoverPoints}.csv"
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
                  mutateProbability, seeds, fileName,
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
    "Method $crossoverPoints " +
    "Replace $replaceCount " +
    "Time ${endTime-startTime}"

outWriter.println("$nodes, $populationPerNode, $queens, $fixedQueens, $crossoverProbability, "
+ "$mutateProbability, $maxGenerations, "
    + "$crossoverPoints, $replaceCount, ${endTime-startTime}")

outWriter.flush()
outWriter.close()
