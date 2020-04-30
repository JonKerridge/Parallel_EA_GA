package sudoku

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int populationPerNode = 5
int geneLength = 9
int nodes = 16
double crossoverProbability = 0.2
double mutateProbability = 1.0
int maxGenerations = 200000
int instances = 3
boolean maximise = true
String fileName = "./Medium.csv"
List <Long> seeds = []

def eDetails = new DataDetails(
    dName: SudukoPopulation.getName(),
    dInitMethod: SudukoPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: SudukoPopulation.createInstance,
    dCreateData: [geneLength, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, null, fileName]
)

def rDetails = new ResultDetails (
    rName: SudokuResult.getName(),
    rInitMethod: SudokuResult.initialise,
    rCollectMethod: SudokuResult.collector,
    rFinaliseMethod: SudokuResult.finalise
)

long startTime = System.currentTimeMillis()

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit( output: chan1.out(),
    eDetails: eDetails )

def eaEngine = new EAGA_Engine<SudukoPopulation>(
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

println " Suduko- nodes, $nodes, " +
    "populationPerNode, $populationPerNode, " +
    " Genes, $geneLength, " +
    "mutateProbability, $mutateProbability, " +
    "crossoverProbability, $crossoverProbability, " +
    "instances, $instances, " +
    "total time, ${endTime-startTime}"


