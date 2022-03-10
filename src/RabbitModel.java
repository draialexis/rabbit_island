import java.util.ArrayList;

public class RabbitModel
{
    private final ArrayList<Rabbit>   rabbits;
    private final ArrayList<Rabbit>   toRemove;
    private final ArrayList<Rabbit>   toAdd;
    static final  MersenneTwisterFast mt;

    static
    {
        // initializing the Mersenne Twister
        mt = new MersenneTwisterFast(new int[]{0x123, 0x234, 0x345, 0x456});
    }

    public RabbitModel(int numFem, int numMal)
    {
        this.toRemove = new ArrayList<>();
        this.toAdd = new ArrayList<>();
        this.rabbits = new ArrayList<>();
        for (int i = 0; i < numFem; i++)
        {
            this.rabbits.add(new Rabbit(Sex.FEMALE));
        }
        for (int i = 0; i < numMal; i++)
        {
            this.rabbits.add(new Rabbit(Sex.MALE));
        }
    }

    void run(int i, int years)
    {
        int    duration = Rabbit.MONTHS_IN_YEAR * years;
        String fileName = "rabbits" + duration + "m_i" + i + ".csv";
        FileStuff.createFile(fileName);
        FileStuff.writeToFile(fileName, "births;deaths");
        for (int j = 1; j <= duration; j++)
        {
            this.stepAge();
            this.stepBirths();
            FileStuff.writeToFile(fileName, Rabbit.births + ";" + Rabbit.deaths);
            if (Rabbit.births == Rabbit.deaths) // extinction... keep that result too? what are we doing here?
            {
                break;
            }
        }
        Rabbit.births = 0;
        Rabbit.deaths = 0;
        // TODO calculate variance, mean, confidence interval etc. for final pops
        // TODO show graphs
    }

    private void stepAge()
    {
        for (Rabbit rabbit : this.rabbits)
        {
            rabbit.ageUp();
            if (rabbit.isDead())
            {
                this.toRemove.add(rabbit);
            }
        }
        this.rabbits.removeAll(this.toRemove);
        this.toRemove.clear();
    }

    private void stepBirths()
    {
        for (Rabbit rabbit : this.rabbits)
        {
            if (rabbit.getSex() == Sex.FEMALE && rabbit.isFertile())
            {
                boolean[] willSpawn       = rabbit.getWillSpawn();
                int       ageWithoutYears = rabbit.getAgeMonths() % Rabbit.MONTHS_IN_YEAR;
                if (willSpawn[ageWithoutYears])
                {
                    if (mt.nextBoolean(Rabbit.DEATH_IN_LABOR_RATE)) // death during labor also kills the offspring
                    {
                        rabbit.kill();
                        this.toRemove.add(rabbit);
                    }
                    else
                    {
                        double rdm = mt.nextGaussian() * Rabbit.STD_DEVIATION_LITTERS_PER_YEAR +
                                     Rabbit.MEAN_LITTERS_PER_YEAR;
                        int    n   = (int) Math.round(rdm);
                        for (int k = 0; k < n; k++)
                        {
                            this.toAdd.add(new Rabbit());
                        }
                    }
                }
            }
        }
        this.rabbits.removeAll(this.toRemove);
        this.toRemove.clear();
        this.rabbits.addAll(this.toAdd);
        this.toAdd.clear();
    }
}