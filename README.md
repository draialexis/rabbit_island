# Rabbits everywhere

+ [About](#about)
+ [Instructions, report (in French)](#instructions-report-in-french)
+ [Warning](#warning)
+ [Compilation and execution](#compilation-and-execution)

## About

**Check this repo's `doc` folder to see the appropriate Javadoc**

Simulating rabbit population growth, with replicate experiments, given a few factors:

* number of replicates
* female to male ratio
* litters per year
* kitten per litter
* kit-birth death rate
* maximum age
* range of possible values for maturity threshold
* female infertility rate
* general mortality rates depending on age
* size of female fertility window
* rudimentary predator role: predator to prey ratio, rate of monthly kills per predator, thresholds

This program then estimates a mean final population count for the given duration, and uses variance and t-distribution
to determine a 99% confidence interval for a _true mean_ for said final count.

The resulting data will be found in `/data_results` after execution

This program uses `MersenneTwisterFast` ([doc](https://javadoc.scijava.org/SciJava/org/scijava/util/MersenneTwisterFast.html))
for pseudorandom number generation.

## Instructions, report (in French)

The default values for the aforementioned factors may be perused
[here](https://github.com/draialexis/sims_tp4/files/8238541/Lab.4.-.Rabbit.Population.growth.pdf), along with the
instructions.

The report can be found [here](https://github.com/draialexis/sims_tp4/files/8804564/tp4_report_v2.pdf)


## Warning

_**Needs further optimization**, please be aware that these rabbits breed like... uh ... llamas or something._ There may
be situations where predators can't keep them down, which means the population will keep growing exponentially, which
means iterating through the model's `run` function will keep taking exponentially more time. This program prevents
populations from rising above `Integer.MAX_VALUE`

## Compilation and execution

`javac -Xlint:all -d build src/com/alexisdrai/popsim/*.java src/com/alexisdrai/util/*.java`

&&

`java -cp build com.alexisdrai.popsim.Main`
