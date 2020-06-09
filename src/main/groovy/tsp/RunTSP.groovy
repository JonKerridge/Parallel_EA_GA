package tsp

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine
import sudoku.SudokuResult
import sudoku.SudukoPopulation

int populationPerNode = 4
int cities = 42
int nodes = 16
double crossoverProbability = 0.75
double mutateProbability = 0.9
int instances = 10
int generations = 10000
boolean maximise = false
String fileName = "./dantzig42.tsp"
int replaceCount = 1000
List <Long> seeds = []
//List <Long> seeds = [105519345081900, 105519345118200, 105519345149700,
//                     105519345179200, 105519345216000, 105519345241700,
//                     105519345170900, 105519345226700]

def eDetails = new DataDetails(
    dName: TSPPopulation.getName(),
    dInitMethod: TSPPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: TSPPopulation.createInstance,
    dCreateData: [cities, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, null, fileName,
                  replaceCount]
)
def rDetails = new ResultDetails (
    rName: TSPResult.getName(),
    rInitMethod: TSPResult.initialise,
    rCollectMethod: TSPResult.collector,
    rFinaliseMethod: TSPResult.finalise
)

long startTime = System.currentTimeMillis()

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit( output: chan1.out(),
    eDetails: eDetails )

def eaEngine = new EAGA_Engine<TSPPopulation>(
    input: chan1.in(),
    output: chan2.out(),
    printGeneration: false,
    generationLimit: generations,
    nodes: nodes
)

def collector = new Collect( input: chan2.in(),
    rDetails: rDetails)

new PAR([emitter, eaEngine, collector]).run()

long endTime = System.currentTimeMillis()

println "TSP Nodes $nodes " +
    "Population $populationPerNode " +
    " Cities $cities " +
    "Crossover $crossoverProbability " +
    "Mutate $mutateProbability " +
    "Instances $instances " +
    "MaxGeneration $generations " +
    "Replace $replaceCount " +
    "Time ${endTime-startTime}"




