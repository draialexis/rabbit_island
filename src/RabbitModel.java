import java.io.*;
import java.util.ArrayList;

public class RabbitModel
{
    private static final int YEARS_TO_RUN = 20;
    private final ArrayList<Rabbit> rabbits;
    private final ArrayList<Rabbit> toRemove;
    private final ArrayList<Rabbit> toAdd;
    static final MersenneTwisterFast mt;

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

    void run(int i)
    {
        int duration = Rabbit.MONTHS_IN_YEAR * YEARS_TO_RUN;
        String fileName = "rabbits" + duration + "m_i" + i + ".csv";
        this.createFile(fileName);
        this.writeToFile(fileName, "births;deaths");
        for (int j = 1; j <= duration; j++)
        {
            this.stepAge();
            this.stepBirths();
            this.writeToFile(fileName, Rabbit.births + ";" + Rabbit.deaths);
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

    private void createFile(String fileName)
    {
        try
        {
            File myObj = new File(fileName);
            if (myObj.createNewFile())
            {
                System.out.println("File created: " + myObj.getName());
            } else
            {
                throw new RuntimeException("file already exists");
            }
        } catch (IOException e)
        {
            System.out.println("file creating error");
            e.printStackTrace();
        }
    }

    private void writeToFile(String fileName, String s)
    {
        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            out.println(s);
        } catch (IOException e)
        {
            System.out.println("file writing error");
            e.printStackTrace();
        }

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
                boolean[] willSpawn = rabbit.getWillSpawn();
                int ageWithoutYears = rabbit.getAgeMonths() % Rabbit.MONTHS_IN_YEAR;
                if (willSpawn[ageWithoutYears])
                {
                    if (mt.nextBoolean(Rabbit.DEATH_IN_LABOR_RATE)) // death during labor also kills the offspring
                    {
                        rabbit.kill();
                        this.toRemove.add(rabbit);
                    } else
                    {
                        int n = (int) Math.round(mt.nextGaussian() * Rabbit.STD_DEVIATION_LITTERS_PER_YEAR +
                                                 Rabbit.MEAN_LITTERS_PER_YEAR);
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