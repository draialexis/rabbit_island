import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

public class RabbitModel
{
    private static int replNum = 1;

    private final LinkedList<Rabbit> rabbits; // linked lists allow for insertions and removals in constant time
    private final LinkedList<Rabbit> toRemove;
    private final LinkedList<Rabbit> toAdd;

    private long births; // storing pop figures as separate attributes to avoid having to call the lists size() methods
    private long deaths; // ... and to have a better detailed vue

    public RabbitModel(int numFem, int numMal)
    {
        this.rabbits = new LinkedList<>();
        this.toRemove = new LinkedList<>();
        this.toAdd = new LinkedList<>();
        this.births = 0;
        this.deaths = 0;
        for (int i = 0; i < numFem + numMal; i++)
        {
            this.births++;
            this.rabbits.add(new Rabbit(i < numFem));
        }
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
        FileStuff.writeToFile(fileName, "births;deaths");// TODO remove before shipping

        //        double ratio    = 0.0, mean = 0.0;
        //        long   prevTime = 0, crtTime;

        for (int j = 1; j <= months; j++)
        {
            //            long start = System.nanoTime();
            //            System.out.println("\nmonth=" + j);
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
                    if (Main.mt.nextBoolean(Rabbit.DEATH_IN_LABOR_RATE)) // death during labor also kills the offspring
                    {
                        unmakeRabbit(rabbit);
                        this.toRemove.add(rabbit);
                    }
                    else
                    {
                        double rdm = Math.round(Main.mt.nextGaussian()
                                                * Rabbit.STD_DEVIATION_KITS_PER_LITTER
                                                + Rabbit.MEAN_KITS_PER_LITTER);
                        int kits = (int) rdm; // explicitly casting long into an int
                        // System.out.print(kits + ", "); // looking for [2;6] normal with mean 4 sigma 0.666
                        for (int k = 0; k < kits; k++)
                        {
                            this.toAdd.add(new Rabbit()); // random sex
                            this.births++;
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

            //            long end = System.nanoTime();
            //            crtTime = end - start;
            //            if (prevTime != 0)
            //            {
            //                ratio = crtTime / (double) prevTime;
            //                mean += ratio;
            //            }
            //            System.out.println("Elapsed Time: " + crtTime + " ns");
            //            System.out.println("Ratio: " + (ratio != 0.0 ? ratio : "N/A"));
            //            System.out.print(ratio + ", ");
            //            System.out.println(crtTime + ", ");
            //            prevTime = crtTime;

            FileStuff.writeToFile(fileName, this.births + ";" + this.deaths);// TODO remove before shipping
        }
        //        mean /= months - 1;
        //        System.out.println("mean ratio=" + mean); // was 1.56... at 70 months
        return this.births - this.deaths;
        // TODO show graphs
    }
}