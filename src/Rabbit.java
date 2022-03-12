public class Rabbit
{
    static final int    MEAN_KITS_PER_LITTER          = 4;                // 4
    static final double STD_DEVIATION_KITS_PER_LITTER = 2 / 3.0;          // 0.6_
    static final double DEATH_IN_LABOR_RATE           = 0.15;             // 0.15

    private static final int      MAX_AGE_MONTHS                 = 156;      // 156
    private static final int      EARLIEST_FERTILITY_START       = 5;        // 5
    private static final int      INTERVAL_FERTILITY_START       = 3;        // 3
    private static final double   FEMALE_FERTILITY_PROB          = 0.9;      // 0.9
    private static final int      MEAN_LITTERS_PER_YEAR          = 6;        // 6
    private static final double   STD_DEVIATION_LITTERS_PER_YEAR = 1.0;      // 1.0
    private static final double   KIT_MORTALITY                  = Math.pow((1 + 0.75), (1 / 12.0)) - 1;
    private static final double[] MONTHLY_MORTALITIES            = new double[MAX_AGE_MONTHS - 12];
    // ignoring year 0->1
    private static final double[] YEARLY_MORTALITIES             = {
            // [0;1[ (will be ignored in static m_m calculations)
            0.25,    // [1;2[
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
    };

    static
    {
        // calculating all constant mortality rates at compile time to improve performance
        int k = 0;
        for (double yearlyMortality : YEARLY_MORTALITIES)
        {
            for (int i = 0; i < 12; i++)
            {
                double m = (Math.pow((1 + yearlyMortality), (1 / 12.0)) - 1);
                MONTHLY_MORTALITIES[k] = m;
                k++;
            }
        }
    }

    private final int       fertilityStart;
    private final boolean   canBeFertile;
    private final boolean[] willSpawn;
    private final char      sex;

    private boolean isFertile;
    private boolean isMature;
    private boolean isDead;
    private int     ageInMonths;
    private int     yearlyDue;
    // the rabbit's year, not the model's -- also, not based on birthday of rabbit, but maturation anniversary

    Rabbit()
    {
        this(Main.mt.nextDouble() < 0.5 ? 'f' : 'm');
    }

    Rabbit(char sex)
    {
        this.fertilityStart = Main.mt.nextInt(INTERVAL_FERTILITY_START + 1) + EARLIEST_FERTILITY_START;
        this.canBeFertile = sex != 'f' || Main.mt.nextBoolean(FEMALE_FERTILITY_PROB);
        this.willSpawn = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };
        this.sex = sex;
        this.isFertile = false;
        this.isMature = false;
        this.isDead = false;
        this.ageInMonths = 0;
        this.yearlyDue = 0;
    }

    public boolean isFertile()
    {
        return this.isFertile;
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    public char getSex()
    {
        return this.sex;
    }

    boolean[] getWillSpawn()
    {
        return willSpawn;
    }

    int getAgeInMonths()
    {
        return ageInMonths;
    }

    public void kill()
    {
        this.isDead = true;
    }

    private void updateDead()
    {
        double rdm = Main.mt.nextDouble();
        if
        (
            // death from old age
                (this.ageInMonths == MAX_AGE_MONTHS)
                ||
                (
                        // death before 1 year: monthly mortality rate depends on maturity, which depends on individual
                        (this.ageInMonths < 12)
                        &&
                        (
                                (this.isMature && (rdm < MONTHLY_MORTALITIES[0])) // same rate as for 1-year-olds
                                ||
                                (rdm < KIT_MORTALITY)
                        )
                )
                ||
                (
                        // death from other monthly mortality rates which were calculated at compile-time
                        (this.ageInMonths >= 12)
                        &&
                        (rdm < MONTHLY_MORTALITIES[this.ageInMonths - 12])
                )
        )
        {
            this.isDead = true;
        }
    }

    private void updateYearlyDue()
    {
        double rdm = (Main.mt.nextGaussian() * STD_DEVIATION_LITTERS_PER_YEAR + MEAN_LITTERS_PER_YEAR);
        this.yearlyDue = (int) Math.round(rdm);

        int toBeSpawned = 0;
        if (this.yearlyDue > 0)
        { // pretty unlikely to be <= 0 with default values, but it could happen
            int period = 12 / this.yearlyDue;
            for (int i = 0; i < 12 && toBeSpawned < this.yearlyDue; i += period)
            {
                willSpawn[i] = true;
                toBeSpawned++;
            }
        }
    }

    public void ageUp()
    {
        this.ageInMonths++;

        if (!(this.isMature) && (this.ageInMonths == this.fertilityStart))
        {
            this.isMature = true;
        }

        this.updateDead();

        if (!(this.isFertile) && this.isMature && this.canBeFertile)
        {
            this.isFertile = true;
        }

        if
        (
                (this.sex == 'f')
                && this.isFertile
                && ((this.ageInMonths - this.fertilityStart) % 12 == 0)
        )
        {
            this.updateYearlyDue();
        }
    }
}
