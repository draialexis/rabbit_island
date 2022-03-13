import java.util.LinkedList;

public class RabbitModel
{
    private static final int PREDATOR_THRESHOLD = 100_000;

    private static int replNum = 1;

    private final LinkedList<Rabbit> rabbits; // linked lists allow for insertions and removals in constant time
    private final LinkedList<Rabbit> toRemove;
    private final LinkedList<Rabbit> bloodedCaerBunnies;
    private final LinkedList<Rabbit> toAdd;

    private long births; // storing pop figures as separate attributes to avoid having to call the lists size() methods
    private long deaths; // ... and to have a better detailed vue
    private int  activePredators;

    public RabbitModel(int numFem, int numMal)
    {
        this.rabbits = new LinkedList<>();
        this.toRemove = new LinkedList<>();
        this.bloodedCaerBunnies = new LinkedList<>();
        this.toAdd = new LinkedList<>();
        this.births = 0;
        this.deaths = 0;
        this.activePredators = 0;
        for (int i = 0; i < numFem + numMal; i++)
        {
            this.rabbits.add(makeRabbit(i < numFem));
        }
    }

    private Rabbit makeRabbit(Boolean isFemale)
    {
        this.births++;
        return new Rabbit(isFemale);
    }

    private Rabbit makeRabbit()
    {
        this.births++;
        return new Rabbit();
    }

    private void unmakeRabbit(Rabbit rabbit)
    {
        this.deaths++;
        rabbit.kill();
    }

    long run(int months)
    {
        String fileName = "rabbits" + months + "m_i" + replNum++ + ".csv";// TODO remove before shipping
        FileStuff.createFile(fileName);// TODO remove before shipping
        FileStuff.writeToFile(fileName, "births;deaths;pop");// TODO remove before shipping

        double ratio    = 0.0, mean = 0.0;
        long   prevTime = 0, crtTime;

        for (int j = 1; j <= months; j++)
        {
            long start = System.nanoTime();
            System.out.println("\nmonth=" + j);
            for (Rabbit rabbit : this.rabbits)
            {
                rabbit.ageUp(); // ageing rabbits first
                if (rabbit.isDead())
                {
                    //                    System.out.print(rabbit.getAgeInMonths() + ", ");
                    unmakeRabbit(rabbit);
                    this.toRemove.add(rabbit);
                    continue; // no need to check further
                }
                // then checking for births
                if
                (
                        rabbit.isFemale()
                        && rabbit.isFertile()
                        && rabbit.getWillGiveBirth()[rabbit.getAgeInMonths() % 12]
                    // accessing willGiveBirth, an individualized 12-month birth planner, to check for due births
                )
                {
                    if (Main.MT.nextBoolean(Rabbit.DEATH_IN_LABOR_RATE)) // death during labor also kills the offspring
                    {
                        unmakeRabbit(rabbit);
                        this.toRemove.add(rabbit);
                    }
                    else
                    {
                        double rdm = Math.round(Main.MT.nextGaussian()
                                                * Rabbit.STD_DEVIATION_KITS_PER_LITTER
                                                + Rabbit.MEAN_KITS_PER_LITTER);
                        int kits = (int) rdm; // explicitly casting long into an int
                        for (int k = 0; k < kits; k++)
                        {
                            this.toAdd.add(makeRabbit());
                        }
                    }
                }
                if (rabbit.isRabbitOfCaerbannog())
                {
                    if (this.births - this.deaths > PREDATOR_THRESHOLD)
                    {
                        // Ok, that's enough rabbits
                        this.bloodedCaerBunnies.add(rabbit);
                        this.activePredators++;
                    }
                    else
                    {
                        if (this.activePredators > 0)
                        {
                            this.bloodedCaerBunnies.clear();
                            // all predators go back to alfalfa and carrots if the population shrinks low enough
                            this.activePredators = 0;
                        }
                    }
                }
            }
            // removing inactive cells to avoid stack-overflow and performance issues
            // using auxiliary lists, for pop evolution, to avoid concurrent modification errors at runtime
            // iterators are cool for removing elements mid-loop, but adding elements gets complicated
            this.rabbits.removeAll(this.toRemove);
            this.toRemove.clear();
            this.rabbits.addAll(this.toAdd);
            this.toAdd.clear();

            int predators = this.bloodedCaerBunnies.size();
            {
                for (int i = 0; i < predators; i++)
                {
                    double rdm = Math.round(Main.MT.nextGaussian()
                                            * Rabbit.STD_DEVIATION_KILLS
                                            + Rabbit.MEAN_KILLS);
                    int kills = (int) rdm;
                    // this should be fine, as long as there are less than 2147483647 rabbits
                    for (int k = 0; k < kills; k++)
                    {
                        if (this.births - this.deaths > PREDATOR_THRESHOLD && this.births - this.deaths <= Main.MAX_INT)
                        {
                            int idx = Main.MT.nextInt((int) (this.births - this.deaths) - 1);
                            unmakeRabbit(this.rabbits.get(idx));
                            this.rabbits.remove(idx);
                            // randomly drawing the victims among all pop --
                            // could technically include themselves or other predators, which is fine
                        }
                    }
                }
            }

            long end = System.nanoTime();
            crtTime = end - start;
            if (prevTime != 0)
            {
                ratio = crtTime / (double) prevTime;
                mean += ratio;
            }
            System.out.println("Elapsed Time: " + crtTime + " ns");
            System.out.println("Ratio: " + (ratio != 0.0 ? ratio : "N/A"));
            prevTime = crtTime;

            String toWrite = this.births + ";" +
                             this.deaths + ";" +
                             (this.births - this.deaths) + ";" +
                             this.activePredators;
            FileStuff.writeToFile(fileName, toWrite);
            // TODO remove before shipping
        }
        mean /= months - 1;
        System.out.println("mean ratio=" + mean);
        /*
         * ...... at 90 months
         * ....... at 90 months with decimating predators
         * ....... at 90 months with double decimating predators
         */
        return this.births - this.deaths;
        // TODO show graphs
    }
}