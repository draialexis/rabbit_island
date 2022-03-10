enum Sex
{
    FEMALE, MALE
}

public class Rabbit
{
    public static long pop = 0;

    private static final short MAX_AGE_MONTHS = 156;
    private static final double[] yearlyMortalities = {0.5, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.4, 0.55, 0.7,
                                                       0.85, 1.0};
    private static final double[] monthlyMortalities = new double[MAX_AGE_MONTHS];

    static
    {
        int k = 0;
        for (double yearlyMortality : yearlyMortalities)
        {
            for (int i = 0; i < 12; i++)
            {
                double m = (Math.pow((1 + yearlyMortality), (1 / 12.0)) - 1);
                System.out.println(k + " : " + m);
                monthlyMortalities[k++] = m;
            }
        }
    }

    private final int fertilityStart;
    private final boolean canBeFertile;

    private boolean isFertile;
    private final Sex sex;
    private short ageMonths;
    private boolean isDead;
    private boolean isMature;
    private int yearlyDue;

    Rabbit()
    {
        this(Math.random() < 0.5 ? Sex.FEMALE : Sex.MALE);
    }

    Rabbit(Sex sex)
    {
        double f = Math.random() / Math.nextDown(1.0);
        this.fertilityStart = (int) (5 * (1.0 - f) + 9 * f);
        // TODO rdm in [5;8] _continuous
        this.canBeFertile = sex != Sex.FEMALE || !(Math.random() >= 0.9);
        // TODO rdm 10% infertile females _continuous
        this.isFertile = false;
        this.sex = sex;
        this.ageMonths = 0;
        this.isDead = false;
        this.isMature = false;
        this.yearlyDue = 0;
        pop++;
    }

    public boolean isFertile()
    {
        return this.isFertile;
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    public Sex getSex()
    {
        return this.sex;
    }

    public int getYearlyDue()
    {
        return yearlyDue;
    }

    public void resetYearlyDue()
    {
        this.yearlyDue = 0;
    }

    public void kill()
    {
        this.isDead = true;
        pop--;
    }

    private void updateMature()
    {
        if (this.ageMonths == this.fertilityStart)
        {
            this.isMature = true;
        }
    }

    private void updateFertile()
    {
        if (this.isMature && this.canBeFertile)
        {
            this.isFertile = true;
        }
    }

    private void updateDead()
    {
        // TODO rdm [0;1] _continuous
        double rdm = Math.random();
        if (this.ageMonths == MAX_AGE_MONTHS || rdm < monthlyMortalities[this.ageMonths])
        {
            this.kill();
        }
    }

    private void updateYearlyDue()
    {
        double f = Math.random() / Math.nextDown(1.0);
        this.yearlyDue = (int) (3 * (1.0 - f) + 10 * f);
        // TODO rdm [3;9] _normal
        // TODO improve that to prevent inaccuracy (use array as litter planner?)
        // as it stands, a female that is due x litters in the next 12 months will spawn them all
        // even if she should die the following month
    }

    public void ageUp()
    {
        this.ageMonths++;

        this.updateDead();

        if (!(this.isMature))
        {
            this.updateMature();
        }
        if (!(this.isFertile))
        {
            this.updateFertile();
        }

        if (this.sex == Sex.FEMALE && this.isFertile && this.ageMonths % 12 == 0)
        {
            // TODO improve that to prevent inaccuracy (use array as litter planner?)
            // as it stands, a female that is due x litters in the next 12 months will spawn them all
            // even if she should die the following month
            this.updateYearlyDue();
        }
    }

    @Override
    public String toString()
    {
        return (this.sex == Sex.FEMALE ? "F_" : "M_");
    }
}
