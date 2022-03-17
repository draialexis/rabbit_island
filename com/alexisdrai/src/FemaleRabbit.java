public final class FemaleRabbit extends Rabbit
{

    private static final double FERTILITY_PROB           = 0.9;         // 0.9
    private static final int    INTERVAL_SIZE_FERTILITY  = 48;          // 48 -- 4 years (added on top)
    private static final int    MEAN_LITTERS_PER_YEAR    = 6;           // 6
    private static final double STD_DEV_LITTERS_PER_YEAR = 1.0;         // 1.0

    private final boolean isPotentiallyFertile;

    private       boolean   isFertile        = false;
    /**
     * an individualized 12-month birth planner
     */
    private final boolean[] pregnancyPlanner = new boolean[]{
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

    FemaleRabbit()
    {
        this.isPotentiallyFertile = Main.MT.nextBoolean(FERTILITY_PROB);
    }

    /**
     * getPregnancyPlanner
     *
     * @return provides a copy of its own {@link #pregnancyPlanner}
     */
    boolean[] getPregnancyPlanner()
    {
        boolean[] copy = new boolean[Main.MONTHS_PER_YEAR];
        System.arraycopy(this.pregnancyPlanner, 0, copy, 0, Main.MONTHS_PER_YEAR);
        return copy;
    }

    boolean isPotentiallyFertile()
    {
        return this.isPotentiallyFertile;
    }

    boolean isFertile()
    {
        return this.isFertile;
    }

    void setFertile(boolean fertile)
    {
        if (!this.isPotentiallyFertile())
        {
            throw new RuntimeException("that rabbit can never be fertile, this method shouldn't have been called on it");
        }
        if (this.isFertile != fertile)
        {
            this.isFertile = fertile;
        }
    }

    /**
     * updatePregnancyPlanner
     * <p>index origin is based on the rabbit's maturation anniversary</p>
     */
    private void updatePregnancyPlanner()
    {
        int fertility_career = this.getAgeInMonths() - this.getMaturityStart();
        // putting the least likely condition first, to get out of the AND conditional structure faster
        if
        (
                fertility_career % Main.MONTHS_PER_YEAR == 0
                && fertility_career < INTERVAL_SIZE_FERTILITY
                && this.isFertile()
        )
        {
            this.wipePregnancyPlanner();
            double rdm = Math.round(Main.MT.nextGaussian()
                                    * STD_DEV_LITTERS_PER_YEAR
                                    + MEAN_LITTERS_PER_YEAR);
            int yearlyDue = (int) rdm; // explicitly casting long into an int

            int toBeSpawned = 0;
            if (yearlyDue > 0)
            { // pretty unlikely to be <= 0 with default values, but it could happen
                int period = Main.MONTHS_PER_YEAR / yearlyDue;
                for (int i = 0; ((i < Main.MONTHS_PER_YEAR) && (toBeSpawned < yearlyDue)); i += period)
                {
                    this.pregnancyPlanner[i] = true;
                    toBeSpawned++;
                }
            }
        }
        if (fertility_career == INTERVAL_SIZE_FERTILITY)
        {
            this.wipePregnancyPlanner();
            this.setFertile(false);
        }
    }

    /**
     * wipePregnancyPlanner
     */
    private void wipePregnancyPlanner()
    {
        for (int i = 0; i < Main.MONTHS_PER_YEAR; i++)
        {
            this.pregnancyPlanner[i] = false;
        }
    }

    /**
     * maturificate
     */
    @Override
    void maturificate()
    {
        super.maturificate();
        if (this.isMature() && !(this.isFertile()) && this.isPotentiallyFertile())
        {
            this.setFertile(true);
        }
    }

    /**
     * ageUp
     * <p>increments a rabbit's age by one month, then checks for death and maturity, and finally plans out yearly litters</p>
     */
    @Override
    void ageUp()
    {
        super.ageUp();
        if (this.isFertile())
        {
            this.updatePregnancyPlanner();
        }
    }
}
