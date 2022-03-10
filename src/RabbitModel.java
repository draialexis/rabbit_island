import java.util.ArrayList;

public class RabbitModel
{
    private final ArrayList<Rabbit> rabbits;
    private final ArrayList<Rabbit> toRemove;
    private final ArrayList<Rabbit> toAdd;

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

    void run()
    {
        for (int i = 1; i <= 12*20; i++)
        {
            this.stepAge();
            this.stepBirths();
            System.out.println("year = " + i / 12+ " month = " + i % 12 + " | pop = " + Rabbit.pop);
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
            int littersDue = rabbit.getYearlyDue();
            for (int j = 0; j < littersDue; j++)
            // not taking the presence of males into account, to improve performance
            {
                if (Math.random() < 0.15)
                {
                    rabbit.kill();
                    this.toRemove.add(rabbit);
                    continue;
                } else
                {
                    double f;
                    int n;
                    f = Math.random() / Math.nextDown(1.0);
                    n = (int) (2 * (1.0 - f) + 7 * f);
                    // TODO rdm in [2;6] _normal
                    if (rabbit.getSex() == Sex.FEMALE && rabbit.isFertile())
                    {
                        for (int k = 0; k < n; k++)
                        {
                            this.toAdd.add(new Rabbit());
                        }
                    }

                }
                rabbit.resetYearlyDue();
                // TODO improve that to prevent inaccuracy (use array as litter planner?)
                // as it stands, a female that is due x litters in the next 12 months will spawn them all
                // even if she should die the following month
            }
        }
        this.rabbits.removeAll(this.toRemove);
        this.toRemove.clear();
        this.rabbits.addAll(this.toAdd);
        this.toAdd.clear();
    }
}
