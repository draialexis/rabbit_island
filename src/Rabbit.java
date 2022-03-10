enum Sex
{
    FEMALE, MALE
}

public class Rabbit
{
    static final int MONTHS_IN_YEAR = 12;
    private static final int MAX_AGE_MONTHS = 156;
    private static final int EARLIEST_FERTILITY_START = 5;
    private static final int INTERVAL_FERTILITY_START = 3;
    private static final double FEMALE_FERTILITY_PROB = 0.9;
    private static final int MEAN_LITTER_COUNT = 6;
    private static final double STD_DEVIATION_LITTER_COUNT = 1.0;
    public static final int MEAN_LITTERS_PER_YEAR = 4;
    public static final double STD_DEVIATION_LITTERS_PER_YEAR = 2 / 3.0;
    public static final double DEATH_IN_LABOR_RATE = 0.15;

    private static final double[] YEARLY_MORTALITIES = {0.25,    // [1;2[
                                                        0.25,    // [2;3[
                                                        0.25,    // [3;4[
                                                        0.25,    // [4;5[
                                                        0.25,    // [5;6[
                                                        0.25,    // [6;7[
                                                        0.25,    // [7;8[
                                                        0.4,     // [8;9[
                                                        0.55,    // [9;10[
                                                        0.7,     // [10;11[
                                                        0.85,    // [11;12[
                                                        1.0      // [12;13[
                                                        // [0;1[ (will be ignored in static m_m calculations)
    };
    private static final double[] MONTHLY_MORTALITIES = new double[MAX_AGE_MONTHS - MONTHS_IN_YEAR];
    // ignoring year 0->1
    static long deaths = 0;
    static long births = 0;

    static
    {
        // calculating all constant mortality rates at compile time to improve performance
        int k = 0;
        for (double yearlyMortality : YEARLY_MORTALITIES)
        {
            for (int i = 0; i < MONTHS_IN_YEAR; i++)
            {
                double m = (Math.pow((1 + yearlyMortality), (1 / 12.0)) - 1);
                MONTHLY_MORTALITIES[k] = m;
                k++;
            }
        }
    }

    private final int fertilityStart;
    private final boolean canBeFertile;
    private final boolean[] willSpawn;
    private final Sex sex;

    private boolean isFertile;
    private boolean isMature;
    private boolean isDead;
    private int ageMonths;
    private int yearlyDue;
    // the rabbit's year, not the model's -- also, not based on birth of rabbit, but its maturation

    Rabbit()
    {
        this(Math.random() < 0.5 ? Sex.FEMALE : Sex.MALE);
    }

    Rabbit(Sex sex)
    {
        this.fertilityStart = RabbitModel.mt.nextInt(INTERVAL_FERTILITY_START + 1) + EARLIEST_FERTILITY_START;
        this.canBeFertile = sex != Sex.FEMALE || RabbitModel.mt.nextBoolean(FEMALE_FERTILITY_PROB);
        this.willSpawn = new boolean[MONTHS_IN_YEAR];
        this.sex = sex;

        this.isFertile = false;
        this.isMature = false;
        this.isDead = false;
        this.ageMonths = 0;
        this.yearlyDue = 0;

        births++;
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

    boolean[] getWillSpawn()
    {
        return willSpawn;
    }

    int getAgeMonths()
    {
        return ageMonths;
    }

    public void kill()
    {
        this.isDead = true;
        deaths++;
    }

    private void updateDead()
    {
        // TODO update styles to make this pimping
        double rdm = RabbitModel.mt.nextDouble(true, true);
        if ((this.ageMonths == MAX_AGE_MONTHS) || ((this.ageMonths < MONTHS_IN_YEAR) &&
                                                   ((this.isMature && (rdm < Math.pow((1 + 0.5), (1 / 12.0)) - 1)) ||
                                                    (rdm < Math.pow((1 + 0.75), (1 / 12.0)) - 1))) ||
            ((this.ageMonths >= 12) && (rdm < MONTHLY_MORTALITIES[this.ageMonths - MONTHS_IN_YEAR])))
        {
            this.kill();
        }
    }

    private void updateYearlyDue()
    {
        this.yearlyDue = (int) Math.round(
                (RabbitModel.mt.nextGaussian() * STD_DEVIATION_LITTER_COUNT + MEAN_LITTER_COUNT));

        int i, toBeSpawned = 0;
        int period = MONTHS_IN_YEAR / this.yearlyDue;
        for (i = 0; i < MONTHS_IN_YEAR && toBeSpawned < this.yearlyDue; i += period)
        {
            willSpawn[i] = true;
            toBeSpawned++;
        }
    }

    public void ageUp()
    {
        this.ageMonths++;

        if (!(this.isMature) && (this.ageMonths == this.fertilityStart))
        {
            this.isMature = true;
        }

        this.updateDead();

        if (!(this.isFertile) && this.isMature && this.canBeFertile)
        {
            this.isFertile = true;
        }

        if ((this.sex == Sex.FEMALE) && this.isFertile &&
            ((this.ageMonths - this.fertilityStart) % MONTHS_IN_YEAR == 0))
        {
            this.updateYearlyDue();
        }
    }
}
