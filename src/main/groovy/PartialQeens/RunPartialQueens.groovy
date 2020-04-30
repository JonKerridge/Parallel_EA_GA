package PartialQeens

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine
import queens.QueensPopulation
import queens.QueensResult

int populationPerNode = 4
int queens = 64
int nodes = 16
int fixedQueens = 4
int maxGenerations = 100000
double crossoverProbability = 1.0
double mutateProbability = 0.4
int mutateRepeats = 2 // used as range to determine the number of repeated mutations
int instances = 5
boolean maximise = false
String fileName = "Fixed$fixedQueens-1.txt"
String outName = "./Q${queens}F${fixedQueens}N${nodes}P${populationPerNode}.csv"
def outFile = new File(outName)
if (outFile.exists())outFile.delete()
def outWriter = outFile.newPrintWriter()

List <Long> seeds = []

def eDetails = new DataDetails(
    dName: PartialQueensPopulation.getName(),
    dInitMethod: PartialQueensPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: PartialQueensPopulation.createInstance,
    dCreateData: [queens, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, null, fileName,
                  fixedQueens, mutateRepeats]
)
def rDetails = new ResultDetails (
    rName: PartialQueensResult.getName(),
    rInitMethod: PartialQueensResult.initialise,
    rInitData: [outWriter],
    rCollectMethod: PartialQueensResult.collector,
    rFinaliseMethod: PartialQueensResult.finalise
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

println "Nodes $nodes " +
    "populationPerNode $populationPerNode " +
    " Queens $queens " +
    "Fixed $fixedQueens " +
    "crossover $crossoverProbability " +
    "mutate $mutateProbability " +
    "mutateRepeats $mutateRepeats " +
    "instances $instances " +
    "maxGeneration $maxGenerations " +
    "total time ${endTime-startTime}"

outWriter.println("$nodes, $populationPerNode, $queens, $fixedQueens, $crossoverProbability, "
+ "$mutateProbability, $mutateRepeats, $maxGenerations, ${endTime-startTime}")
outWriter.flush()
outWriter.close()
