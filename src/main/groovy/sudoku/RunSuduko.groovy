package sudoku

import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int populationPerNode = 6       //6 for seeds as specified completers in 16252 generations
int geneLength = 9
int nodes = 16
double crossoverProbability = 0.75
double mutateProbability = 0.5
int maxGenerations = 40000
int instances = 5
int replaceCount = 10000
boolean maximise = true
String fileName = "./Easy.csv"
//List <Long> seeds = []

List <Long> seeds =
    [974959942427100, 974959941993400, 974959942153300, 974959941431900, 974959942111900, 974959941990600, 974959942587300, 974959942663500, 974959942815500, 974959942353600, 974959942475300, 974959942164000, 974959942746900, 974959942534700, 974959942216100, 974959942861900]
def eDetails = new DataDetails(
    dName: SudukoPopulation.getName(),
    dInitMethod: SudukoPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: SudukoPopulation.createInstance,
    dCreateData: [geneLength, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, seeds, fileName, replaceCount]
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


