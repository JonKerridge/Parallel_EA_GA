package tsp

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine
import parallel_ea_ga.EAGA_Engine_Record
import sudoku.SudokuResult
import sudoku.SudukoPopulation

int populationPerNode = 4
int cities = 42
int nodes = 15
double crossoverProbability = 1.0
double mutateProbability = 0.3
int instances = 10
int generations = 100000
boolean maximise = false
String fileName = "./dantzig42.tsp"

List <Long> seeds = [484287167751600, 484287167764400, 484287167776600,
                     484287167788900, 484287167801000, 484287167813100,
                     484287167825100, 484287167837500, 484287167849800,
                     484287167862300, 484287167874300, 484287167886500,
                     484287167898800, 484287167911100, 484287167924200]

def eDetails = new DataDetails(
    dName: TSPPopulationRecord.getName(),
    dInitMethod: TSPPopulationRecord.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: TSPPopulationRecord.createInstance,
    dCreateData: [cities, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, seeds, fileName]
)
def rDetails = new ResultDetails (
    rName: TSPResultRecord.getName(),
    rInitMethod: TSPResultRecord.initialise,
    rCollectMethod: TSPResultRecord.collector,
    rFinaliseMethod: TSPResultRecord.finalise
)

long startTime = System.currentTimeMillis()

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit( output: chan1.out(),
    eDetails: eDetails )

def eaEngine = new EAGA_Engine_Record<TSPPopulationRecord>(
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

println " TSP, $nodes, " +
    "populationPerNode, $populationPerNode, " +
    "Cities, $cities, " +
    "crossover, $crossoverProbability, " +
    "mutate, $mutateProbability, " +
    "instances, $instances, " +
    "total time, ${endTime-startTime}"


