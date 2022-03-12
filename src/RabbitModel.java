import java.util.LinkedList;

public class RabbitModel
{
    // private static int replNum = 1;

    private final LinkedList<Rabbit> rabbits;
    private final LinkedList<Rabbit> toRemove;
    private final LinkedList<Rabbit> toAdd;

    private long births;
    private long deaths;

    public RabbitModel(int numFem, int numMal)
    {
        char sex;
        this.rabbits = new LinkedList<>();
        this.toRemove = new LinkedList<>();
        this.toAdd = new LinkedList<>();
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
        if (sex == 'r') // random
        {
            return new Rabbit();
        }
        else
        {
            if (sex == 'f' || sex == 'm') // male or female
            {
                return new Rabbit(sex);
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

    long run(int months)
    {
        //        String fileName = "rabbits" + months + "m_i" + replNum++ + ".csv";// TODO remove before shipping
        //        FileStuff.createFile(fileName);// TODO remove before shipping
        //        FileStuff.writeToFile(fileName, "births;deaths");// TODO remove before shipping
        for (int j = 1; j <= months; j++)
        {
            for (Rabbit rabbit : this.rabbits)
            {
                rabbit.ageUp(); // ageing rabbits first
                if (rabbit.isDead())
                {
                    unmakeRabbit(rabbit);
                    this.toRemove.add(rabbit);
                }
                // then checking for births
                if
                (
                        !(rabbit.isDead())
                        && rabbit.getSex() == 'f'
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
                            this.toAdd.add(makeRabbit('r')); // random sex
                        }
                    }
                }
            }
            // using auxiliary lists, for pop evolution, to avoid concurrent modification errors at runtime
            // & removing inactive cells to avoid stack-overflow and performance issues
            this.rabbits.removeAll(this.toRemove);
            this.toRemove.clear();
            this.rabbits.addAll(this.toAdd);
            this.toAdd.clear();

            //            FileStuff.writeToFile(fileName, this.births + ";" + this.deaths);// TODO remove before shipping
        }
        return this.births - this.deaths;
        // TODO show graphs
    }
}