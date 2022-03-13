public class Rabbit
{
    static final int    MEAN_KITS_PER_LITTER          = 4;                      // 4
    static final double STD_DEVIATION_KITS_PER_LITTER = 2 / 3.0;                // 0.6_ (2 / 3.0)
    static final double DEATH_IN_LABOR_RATE           = 0.15;                   // 0.15
    static final double MEAN_KILLS                    = 5000.0;                 // 5000.0 (added on top)
    static final double STD_DEVIATION_KILLS           = 1000 / 3.0;             // 333.3_ (1000/3.0) (added on top)
    // externalized some attributes, trying to sacrifice robustness for speed

    private static final int      MAX_AGE_MONTHS                 = 120;         // 120 -- 10 years (suggested 13 years)
    private static final int      EARLIEST_FERTILITY_START       = 5;           // 5
    private static final int      INTERVAL_SIZE_FERTILITY_START  = 3;           // 3
    private static final int      INTERVAL_SIZE_FERTILITY        = 48;          // 48 -- 4 years (added on top)
    private static final double   FEMALE_RATIO                   = 0.5;         // 0.5
    private static final double   FEMALE_FERTILITY_PROB          = 0.9;         // 0.9
    private final static double   CAERBANNOG_RATIO               = 1 / 32768.0; // 0.00003 (1 in 2^15) (added on top)
    private static final int      MEAN_LITTERS_PER_YEAR          = 6;           // 6
    private static final double   STD_DEVIATION_LITTERS_PER_YEAR = 1.0;         // 1.0
    private static final double   KIT_YEARLY_MORTALITY           = 0.75;        // 0.75
    private static final double   KIT_MONTHLY_MORTALITY          = Math.pow((1 + KIT_YEARLY_MORTALITY), (1 / 12.0)) - 1;
    private static final double[] YEARLY_MORTALITIES             = {
            // [0;1[ (will be ignored in static monthly mortality rate calculations)
            0.25,    // [1;2[
            0.25,    // [2;3[
            0.25,    // [3;4[
            0.25,    // [4;5[
            //            0.25,    // [5;6[
            //            0.25,    // [6;7[
            //            0.25,    // [7;8[
            0.4,     // [5;6[   // [8;9[
            0.55,    // [6;7[   // [9;10[
            0.7,     // [7;8[   // [10;11[
            0.85,    // [8;9[   // [11;12[
            1.0      // [9;10[  // [12;13[
    };

    private static final double[] MONTHLY_MORTALITIES = new double[MAX_AGE_MONTHS - 12];
    // ignoring year 0->1

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
    private final boolean[] willGiveBirth;
    private final boolean   isFemale;
    private final boolean   isRabbitOfCaerbannog;

    private boolean isFertile;
    private boolean isMature;
    private boolean isDead;
    private int     ageInMonths;
    private int     yearlyDue;
    // the rabbit's year, not the model's -- also, not based on birthday of rabbit, but maturation anniversary

    Rabbit()
    {
        this(Main.MT.nextBoolean(FEMALE_RATIO));
    }

    Rabbit(boolean isFemale)
    {
        this.fertilityStart = Main.MT.nextInt(INTERVAL_SIZE_FERTILITY_START + 1) + EARLIEST_FERTILITY_START;
        this.canBeFertile = !isFemale || Main.MT.nextBoolean(FEMALE_FERTILITY_PROB);
        this.willGiveBirth = new boolean[]{
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
        this.isRabbitOfCaerbannog = Main.MT.nextBoolean(CAERBANNOG_RATIO);
        this.isFemale = isFemale;
        this.isFertile = false;
        this.isMature = false;
        this.isDead = false;
        this.ageInMonths = 0;
        this.yearlyDue = 0;
    }

    boolean isRabbitOfCaerbannog()
    {
        return isRabbitOfCaerbannog;
    }

    public boolean isFemale()
    {
        return this.isFemale;
    }

    public boolean isFertile()
    {
        return this.isFertile;
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    boolean[] getWillGiveBirth()
    {
        return willGiveBirth;
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
        double rdm = Main.MT.nextDouble();
        //putting the likeliest candidates for death first, to get out of the OR conditional structure faster
        if
        (
                (
                        // death before 1 year: monthly mortality rate depends on maturity, which depends on individual
                        (this.ageInMonths < 12)
                        &&
                        (
                                (this.isMature && (rdm < MONTHLY_MORTALITIES[0])) // same rate as for 1-year-olds
                                ||
                                (rdm < KIT_MONTHLY_MORTALITY)
                        )
                )
                ||
                (
                        // death from other monthly mortality rates which were calculated at compile-time
                        (12 <= this.ageInMonths && this.ageInMonths < MAX_AGE_MONTHS)
                        &&
                        (rdm < MONTHLY_MORTALITIES[this.ageInMonths - 12])
                )
                ||
                // death from old age
                (this.ageInMonths == MAX_AGE_MONTHS)
        )
        {
            this.isDead = true;
        }
    }

    private void updateYearlyDue()
    {
        this.resetWillGiveBirth();
        double rdm = Math.round(Main.MT.nextGaussian()
                                * STD_DEVIATION_LITTERS_PER_YEAR
                                + MEAN_LITTERS_PER_YEAR);
        this.yearlyDue = (int) rdm; // explicitly casting long into an int

        int toBeSpawned = 0;
        if (this.yearlyDue > 0)
        { // pretty unlikely to be <= 0 with default values, but it could happen
            int period = 12 / this.yearlyDue;
            for (int i = 0; i < 12 && toBeSpawned < this.yearlyDue; i += period)
            {
                this.willGiveBirth[i] = true;
                toBeSpawned++;
            }
        }
    }

    private void resetWillGiveBirth()
    {
        for (int i = 0; i < 12; i++)
        {
            this.willGiveBirth[i] = false;
        }
    }

    public void ageUp()
    {
        this.ageInMonths++;

        //putting the least likely condition first, to get out of the AND conditional structure faster
        if ((this.ageInMonths == this.fertilityStart) && !(this.isMature)) // maturation
        {
            this.isMature = true;
            if (!(this.isFertile) && this.canBeFertile) // fertility
            {
                this.isFertile = true;
            }
        }

        this.updateDead(); // checking for age-related deaths

        //putting the least likely condition first, to get out of the AND conditional structure faster
        int fertility_career = this.ageInMonths - this.fertilityStart;
        if
        (
                fertility_career % 12 == 0 // pregnancies
                && fertility_career < INTERVAL_SIZE_FERTILITY
                && this.isFemale
                && this.isFertile
        )
        {
            this.updateYearlyDue();
        }
        if (fertility_career == INTERVAL_SIZE_FERTILITY && this.isFemale)
        {
            this.resetWillGiveBirth();
            this.isFertile = false;
        }
    }
}
