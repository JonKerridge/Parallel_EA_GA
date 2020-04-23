package queens

import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine
import parallel_ea_ga.EAGA_Engine_Record

int populationPerNode = 4
int queens = 1024
int nodes = 15
double crossoverProbability = 1.0
double mutateProbability = 0.4
int instances = 5
boolean maximise = false
String fileName = ""

List <Long> seeds = [305180032047500, 305180032055200, 305180032055600,
                     305180032079700, 305180032047400, 305180032059700,
                     305180032073700, 305180032088100, 305180032099800,
                     305180032112700, 305180032128300, 305180032141700,
                     305180032154100, 305180032165900, 305180032183300]

def eDetails = new DataDetails(
    dName: QueensPopulationRecord.getName(),
    dInitMethod: QueensPopulationRecord.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: QueensPopulationRecord.createInstance,
    dCreateData: [queens, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, seeds, fileName]
)
def rDetails = new ResultDetails (
    rName: QueensResultRecord.getName(),
    rInitMethod: QueensResultRecord.initialise,
    rCollectMethod: QueensResultRecord.collector,
    rFinaliseMethod: QueensResultRecord.finalise
)

long startTime = System.currentTimeMillis()

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit( output: chan1.out(),
    eDetails: eDetails )

def eaEngine = new EAGA_Engine_Record<QueensPopulationRecord>(
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
