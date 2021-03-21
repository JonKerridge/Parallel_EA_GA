package maxOnes

import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import jcsp.lang.Channel
import parallel_ea_ga.EAGA_Engine

int populationPerNode = 4
int ones = 2048
int nodes = 24
double crossoverProbability = 1.0
double mutateProbability = 0.8
int instances = 10
int generations = 10000
int replaceCount = 1000
boolean maximise = true
String fileName = ""
List <Long> seeds = null
//List<Long> seeds = [95453092031400,	95453092041100,	95453092053200,
//                    95453092065700,	95453092078000,	95453092090500,
//                    95453092102600,	95453092114600,	95453092127000,
//                    95453092139200,	95453092151700,	95453092164100,
//                    95453092176600,	95453092189500,	95453092205500,	95453092220300 ]

String outBase = "D:\\EAGAoutputs" + "/Ones${ones}Repeat/"
def dir = new File(outBase)
dir.mkdirs()
String outName = outBase +
    "N${nodes}P${populationPerNode}" +
    "XP${crossoverProbability}MP${mutateProbability}Repeat.csv"
println "Out File = $outName"

def outFile = new File(outName)
if (outFile.exists())outFile.delete()
def outWriter = outFile.newPrintWriter()


def eDetails = new DataDetails(dName: MaxOnePopulation.getName(),
    dInitMethod: MaxOnePopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: MaxOnePopulation.createInstance,
    dCreateData: [ones, populationPerNode,
                  nodes, maximise, crossoverProbability,
                  mutateProbability, seeds, fileName,
                  replaceCount])

def rDetails = new ResultDetails(rName: MaxOneResult.getName(),
    rInitMethod: MaxOneResult.initialise,
    rInitData: [outWriter],
    rCollectMethod: MaxOneResult.collector,
    rFinaliseMethod: MaxOneResult.finalise)

long startTime = System.currentTimeMillis()

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()


def emitter = new Emit(output: chan1.out(),
    eDetails: eDetails)

def eaEngine = new EAGA_Engine<MaxOnePopulation>(input: chan1.in(),
    output: chan2.out(),
    printGeneration: false,
    generationLimit: generations,
    nodes: nodes)

def collector = new Collect(input: chan2.in(),
    rDetails: rDetails)

new PAR([emitter, eaEngine, collector]).run()

long endTime = System.currentTimeMillis()

println "MaxOnes Nodes $nodes " +
    "populationPerNode $populationPerNode " +
    "Ones $ones " +
    "crossover $crossoverProbability " +
    "mutate $mutateProbability " +
     "instances $instances " +
    "maxGeneration $generations " +
    "replace $replaceCount " +
    "total time ${endTime-startTime}"

outWriter.println("$nodes, $populationPerNode, $ones, $crossoverProbability, "
    + "$mutateProbability, $generations, "
    + "$replaceCount, ${endTime-startTime}")

outWriter.flush()
outWriter.close()



