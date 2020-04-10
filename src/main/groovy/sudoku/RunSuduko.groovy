package sudoku

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int populationPerNode = 4
int geneLength = 9
int nodes = 8
double crossoverProbability = 1.0
double mutateProbability = 0.5
int instances = 3
boolean maximise = true
String fileName = "./Easy.csv"
//List <Long> seeds = [1122334455L, 6677889900L, 2233445566L, 7788990011L,
//                     3344556677L, 8899001122L, 4455667788L, 9900112233L,
//                     1234567890L, 2345678901L, 3456789012L, 4567890123L,
//                     5678901234L, 6789012345L, 7890123456L, 8901234567L]

List <Long> seeds = [105519345081900, 105519345118200, 105519345149700,
                     105519345179200, 105519345216000, 105519345241700,
                     105519345170900, 105519345226700]

def eDetails = new DataDetails(
    dName: SudukoPopulation.getName(),
    dInitMethod: SudukoPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: SudukoPopulation.createInstance,
    dCreateData: [geneLength, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, seeds, fileName]
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
    generationLimit: 300000,
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
    "instances, $instances, " +
    "total time, ${endTime-startTime}"


