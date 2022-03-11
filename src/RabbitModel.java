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

    private long births;
    private long deaths;

    public RabbitModel(int numFem, int numMal)
    {
        char sex;
        this.toRemove = new ArrayList<>();
        this.toAdd = new ArrayList<>();
        this.rabbits = new ArrayList<>();
        this.births = 0;
        this.deaths = 0;
        for (int i = 0; i < numFem + numMal; i++)
        {
            if (i < numFem)
            {
                sex = 'f';
            }
            else
            {
                sex = 'm';
            }
            this.rabbits.add(makeRabbit(sex));
        }
    }

    private Rabbit makeRabbit(char sex)
    {
        this.births++;
        if (sex == 'f' || sex == 'm') // male or female
        {
            return new Rabbit(sex);
        }
        else
        {
            if (sex == 'r') // random
            {
                return new Rabbit();
            }
            else
            {
                throw new RuntimeException("can't make a rabbit like that");
            }
        }
    }

    private void unmakeRabbit(Rabbit rabbit)
    {
        this.deaths++;
        rabbit.kill();
    }

    long run(int i, int years)
    {
        int    duration = Rabbit.MONTHS_IN_YEAR * years;// TODO remove before shipping
        String fileName = "rabbits" + duration + "m_i" + i + ".csv";// TODO remove before shipping

        FileStuff.createFile(fileName);// TODO remove before shipping
        FileStuff.writeToFile(fileName, "births;deaths");// TODO remove before shipping

        for (int j = 1; j <= duration; j++)
        {
            this.stepAge();
            this.stepBirths();
            FileStuff.writeToFile(fileName, this.births + ";" + this.deaths);// TODO remove before shipping
        }
        return this.births - this.deaths;
        // TODO show graphs
    }

    private void stepAge()
    {
        for (Rabbit rabbit : this.rabbits)
        {
            rabbit.ageUp();
            if (rabbit.isDead())
            {
                unmakeRabbit(rabbit);
                this.toRemove.add(rabbit);
            }
        }
        this.rabbits.removeAll(this.toRemove); // removing inactive cells to avoid stack-overflow and performance issues
        this.toRemove.clear();
    }

    private void stepBirths()
    {
        for (Rabbit rabbit : this.rabbits)
        {
            if (rabbit.getSex() == 'f' && rabbit.isFertile())
            {
                boolean[] willSpawn       = rabbit.getWillSpawn();
                int       ageWithoutYears = rabbit.getAgeMonths() % Rabbit.MONTHS_IN_YEAR;
                if (willSpawn[ageWithoutYears])
                {
                    if (mt.nextBoolean(Rabbit.DEATH_IN_LABOR_RATE)) // death during labor also kills the offspring
                    {
                        unmakeRabbit(rabbit);
                        this.toRemove.add(rabbit);
                    }
                    else
                    {
                        double rdm = mt.nextGaussian() * Rabbit.STD_DEVIATION_LITTERS_PER_YEAR +
                                     Rabbit.MEAN_LITTERS_PER_YEAR;
                        int n = (int) Math.round(rdm);
                        for (int k = 0; k < n; k++)
                        {
                            this.toAdd.add(makeRabbit('r'));
                        }
                    }
                }
            }
        }
        this.rabbits.removeAll(this.toRemove);// removing inactive cells to avoid stack-overflow and performance issues
        this.toRemove.clear();
        this.rabbits.addAll(this.toAdd);
        this.toAdd.clear();
    }
}