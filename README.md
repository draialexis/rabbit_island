# Rabbits everywhere
Simulating rabbit population growth, with replicate experiments, given a few factors:
* litters per year
* kitten per litter
* kit-birth death rate
* maximum age
* range of possible values for maturity threshold
* female infertility rate
* general mortality rates depending on age

This program uses [`MersenneTwisterFast`](https://javadoc.scijava.org/SciJava/org/scijava/util/MersenneTwisterFast.html) for pseudorandom number generation.

The default values for the aforementioned factors may be perused in the `Rabbit` class -- or [here](https://github.com/draialexis/sims_tp4/files/8238541/Lab.4.-.Rabbit.Population.growth.pdf), along with the instructions (in French).

##Warning
_**Needs further optimization**, please be aware that running 50 replicates of an 80-month experiment took 15 minutes on a 3.6GHz CPU / 16Gb RAM machine._

## Compilation and execution

`javac -Xlint:all -d build src/*.java`

&&

`java -cp build Main`
