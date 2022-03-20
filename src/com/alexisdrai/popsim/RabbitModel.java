package com.alexisdrai.popsim;

import com.alexisdrai.util.FileStuff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import static java.util.Objects.requireNonNull;

/**
 * <p>has fields and methods meant to model a {@link Rabbit} population roughly, including growing and shrinking</p>
 */
public final class RabbitModel
{
    private static final int    PREDATOR_THRESHOLD        = 50_000;                   // 50000 (added on top)
    private static final double MEAN_KILLS                = 1500;                     // 1500 (added on top)
    private static final double STD_DEVIATION_KILLS       = 1000 / 3.0;               // 333.3_ (added on top)
    private static final double FEMALE_RATIO              = 0.5;                      // 0.5
    private static final int    MEAN_KITTEN_PER_LITTER    = 4;                        // 4
    private static final double STD_DEV_KITTEN_PER_LITTER = 2 / 3.0;                  // 0.6_
    private static final double DEATH_IN_LABOR_RATE       = 0.15;                     // 0.15

    private static int nbOfReplicates = 1;

    private final Set<Rabbit>       rabbits = new HashSet<>();
    private final ArrayList<Rabbit> toAdd   = new ArrayList<>();

    // storing pop figures as separate attributes to avoid having to call the collections' size() methods
    // ... and also, in order to have a slightly better-detailed set of data
    private long    deaths             = 0;
    private long    births             = 0;
    private int     predators          = 0;
    private boolean arePredatorsActive = false;

    public RabbitModel(int numFem, int numMal)
    {
        for (int i = 0; i < numFem + numMal; i++)
        {
            // making as many female ones as requested, then the rest can be male
            this.rabbits.add(makeRabbit(i < numFem));
        }
    }

    long getDeaths()
    {
        return this.deaths;
    }

    long getBirths()
    {
        return this.births;
    }

    int getPredators()
    {
        return this.predators;
    }

    boolean arePredatorsActive()
    {
        return this.arePredatorsActive;
    }

    private void setPredatorsActive(boolean arePredatorsActive)
    {
        this.arePredatorsActive = arePredatorsActive;
    }

    /**
     * <p>simply calls {@link #makeRabbit(boolean)} with a random boolean
     * based on {@link #FEMALE_RATIO}</p>
     *
     * @return a {@link Rabbit} instance with randomized sex
     */
    private Rabbit makeRabbit()
    {
        return this.makeRabbit(Main.MT.nextBoolean(FEMALE_RATIO));
    }

    /**
     * <p>increments the number of births for this model and instantiates a new
     * {@link Rabbit} of a predetermined sex.</p>
     * <p>if that was a predator rabbit <small>(don't question it)</small>,
     * it increments the number of predators as well.</p>
     * <p><em>note that {@link FemaleRabbit} are a subclass of {@link Rabbit}</em></p>
     *
     * @param isFemale said predetermined sex: {@code true} for {@link FemaleRabbit}, {@code false} for {@link Rabbit}
     * @return a {@link Rabbit} instance of the determined sex
     */
    private Rabbit makeRabbit(boolean isFemale)
    {
        Rabbit rabbit;
        this.births++;
        if (isFemale)
        {
            rabbit = new FemaleRabbit();
        }
        else
        {
            rabbit = new Rabbit();
        }
        if (rabbit.isRabbitOfCaerbannog())
        {
            this.predators++;
        }
        return rabbit;
    }

    /**
     * <p>increments the number of deaths for this model and invokes a given {@link Rabbit}'s
     * {@link Rabbit#kill()} method</p>
     * <p>if that was a predator rabbit <small>(don't question it)</small>,
     * decrements the number of predators as well</p>
     *
     * @param rabbit the {@link Rabbit} instance to be destroyed
     */
    private void destroyRabbit(Rabbit rabbit)
    {
        this.deaths++;
        requireNonNull(rabbit).kill();
        if (rabbit.isRabbitOfCaerbannog())
        {
            this.predators--;
        }
    }

    /**
     * <p>calculates the current population total for this model</p>
     *
     * @return the current population total
     */
    private int getPop()
    {
        long pop = this.getBirths() - this.getDeaths();
        if (pop > Integer.MAX_VALUE)
        {
            throw new RuntimeException("We're looking at " + pop + " rabbits. Something went wrong. Better stop now");
        }
        return (int) pop;
    }

    /**
     * <p>decreases the population total by a number through the use of "predators"</p>
     */
    private void cull()
    {
        if (this.getPredators() > 0)
        {
            for (int i = 0; i < this.getPredators(); i++)
            {
                if (this.getPop() < PREDATOR_THRESHOLD)
                {
                    // under the pop threshold, all predators go back to alfalfa and carrots
                    this.setPredatorsActive(false);
                    return;
                }
                int j = 0;
                int kills = (int) Math.round(Main.MT.nextGaussian()
                                             * STD_DEVIATION_KILLS
                                             + MEAN_KILLS);
                // this casting should be fine, since there are less than Integer.MAX_VALUE rabbits

                Iterator<Rabbit> it = this.rabbits.iterator();
                while (j < kills && it.hasNext())
                {
                    // counting on HashSets being randomly arranged for randomly drawing the victims among all pop --
                    this.destroyRabbit(it.next());
                    it.remove();
                    j++;
                }
            }
        }
    }

    /**
     * <p>runs the {@link RabbitModel} through a statically given number of steps (months),
     * while recording resulting data to text files</p>
     *
     * @return the final population total
     */
    int run()
    {
        if (nbOfReplicates > 1)
        {
            System.out.println("done");
        }
        String fileName = "data_results/rabbits" + Main.TOTAL_MONTHS + "m_i" + nbOfReplicates + ".csv";
        FileStuff.createFile(fileName);
        FileStuff.writeToFile(fileName, "births;deaths;pop;predators");
        System.out.print("creating " + fileName + "... ");

        for (int i = 1; i <= Main.TOTAL_MONTHS; i++)
        {
            Iterator<Rabbit> it = this.rabbits.iterator();
            while (it.hasNext())
            {
                Rabbit rabbit = it.next();
                // ageing rabbits first
                if (!rabbit.isDead())
                {
                    rabbit.ageUp();
                }
                // isDead() might have just been updated
                // removing inactive elements ASAP
                if (rabbit.isDead())
                {
                    this.destroyRabbit(rabbit);
                    it.remove();
                    continue; // we're done with this rabbit
                }
                // checking for births due in that specific rabbit's month
                if
                (
                        rabbit instanceof FemaleRabbit &&
                        ((FemaleRabbit) rabbit).getPregnancyPlanner()[rabbit.getAgeInMonths() % Main.MONTHS_PER_YEAR]
                )
                {
                    // checking for deaths during labor
                    // which also prevents the would-be offspring from being born
                    if (Main.MT.nextBoolean(DEATH_IN_LABOR_RATE))
                    {
                        this.destroyRabbit(rabbit);
                        it.remove();
                        continue; // rip
                    }
                    else
                    {
                        int kitten = (int) Math.round(Main.MT.nextGaussian()
                                                      * STD_DEV_KITTEN_PER_LITTER
                                                      + MEAN_KITTEN_PER_LITTER);
                        // explicitly casting long into an int, should be fine with the numbers we expect
                        for (int j = 0; j < kitten; j++)
                        {
                            this.toAdd.add(this.makeRabbit());
                        }
                    }
                }
                // dealing with overpopulation using predators
                if (this.getPop() >= PREDATOR_THRESHOLD)
                {
                    this.setPredatorsActive(true);
                }
            }
            // to avoid concurrent modification errors at runtime when adding elements
            this.rabbits.addAll(this.toAdd);
            this.toAdd.clear();

            if (this.arePredatorsActive())
            {
                this.cull();
            }
            String toWrite = this.getBirths() + ";" +
                             this.getDeaths() + ";" +
                             this.getPop() + ";" +
                             this.getPredators();
            FileStuff.writeToFile(fileName, toWrite);
        }
        nbOfReplicates++;
        return this.getPop();
    }
}