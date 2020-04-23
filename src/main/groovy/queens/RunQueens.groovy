package queens

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int populationPerNode = 12
int queens = 1024
int nodes = 15
double crossoverProbability = 1.0
double mutateProbability = 0.4
int instances = 5
boolean maximise = false
String fileName = ""

List <Long> seeds = []

def eDetails = new DataDetails(
    dName: QueensPopulation.getName(),
    dInitMethod: QueensPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: QueensPopulation.createInstance,
    dCreateData: [queens, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, null, fileName]
)
def rDetails = new ResultDetails (
    rName: QueensResult.getName(),
    rInitMethod: QueensResult.initialise,
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
    generationLimit: 200000,
    nodes: nodes
)

def collector = new Collect( input: chan2.in(),
    rDetails: rDetails)

new PAR([emitter, eaEngine, collector]).run()

long endTime = System.currentTimeMillis()

println " Queens- nodes, $nodes, " +
    "populationPerNode, $populationPerNode, " +
    " Queens, $queens, " +
    "crossover, $crossoverProbability, " +
    "mutate, $mutateProbability, " +
    "instances, $instances, " +
    "total time, ${endTime-startTime}"
