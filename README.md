# Rabbits everywhere

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
* rudimentary predator role: predator to prey ratio, rate of monthly kills per predator, thresholds

This program then estimates a mean final population count for the given duration, and uses variance and t-distribution
to determine a 99% confidence interval for a _true mean_ for said final count.

This program uses [`MersenneTwisterFast`](https://javadoc.scijava.org/SciJava/org/scijava/util/MersenneTwisterFast.html)
for pseudorandom number generation.

The default values for the aforementioned factors may be perused
[here](https://github.com/draialexis/sims_tp4/files/8238541/Lab.4.-.Rabbit.Population.growth.pdf), along with the
instructions (in French).

## Warning

_**Needs further optimization**, please be aware that these rabbits breed like... uh ... llamas or something. There may
be situations where predators can't keep them down, which means the population will keep growing exponentially, which
means iterating through the `run` function will take exponentially more time._

## Compilation and execution

`javac -Xlint:all -d build src/*.java`

&&

`java -cp build Main`
