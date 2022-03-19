package com.alexisdrai.popsim;

/**
 * <p>has all fields and methods of the {@link Rabbit} class, adding onto it features pertaining to birthing and
 * fertility</p>
 */
public final class FemaleRabbit extends Rabbit
{
    private static final double FERTILITY_PROB           = 0.9;         // 0.9
    private static final int    INTERVAL_SIZE_FERTILITY  = 48;          // 48 -- 4 years (added on top)
    private static final int    MEAN_LITTERS_PER_YEAR    = 6;           // 6
    private static final double STD_DEV_LITTERS_PER_YEAR = 1.0;         // 1.0

    private final boolean isPotentiallyFertile;

    private boolean isFertile = false;

    /**
     * <p>an individualized 12-month birth planner, in the form of an array of booleans</p>
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
     * @return provides a copy of a {@link FemaleRabbit}'s {@link #pregnancyPlanner}
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
        if (this.isPotentiallyFertile() && this.isFertile != fertile)
        {
            this.isFertile = fertile;
        }
    }

    /**
     * <p>updates a {@link FemaleRabbit}'s {@link #pregnancyPlanner}; index origin is based on the rabbit's maturation anniversary</p>
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
     * <p>empties all of a {@link FemaleRabbit}'s {@link #pregnancyPlanner} and sets its elements to {@code false} </p>
     */
    private void wipePregnancyPlanner()
    {
        for (int i = 0; i < Main.MONTHS_PER_YEAR; i++)
        {
            this.pregnancyPlanner[i] = false;
        }
    }

    /**
     * <p>simply sets this {@link FemaleRabbit}'s {@code isMature} property to true, if it turns out to be of age and not mature yet.</p>
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
     * <p>increments a {@link FemaleRabbit}'s {@code ageInMonths} by one month, then calls checks for death and maturity, and finally plans out yearly litters</p>
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
