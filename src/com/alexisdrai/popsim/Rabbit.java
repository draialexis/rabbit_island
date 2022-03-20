package com.alexisdrai.popsim;

/**
 * <p>has fields and methods meant to model a rabbit roughly, including aging, being born, and preying on other rabbits</p>
 * <p>OK, that's not exactly <em>rabbit-like</em>, but that choice has been made</p>
 */
public class Rabbit
{
    private static final int      EARLIEST_MATURITY_START      = 5;           // 5
    private static final int      INTERVAL_SIZE_MATURITY_START = 3;           // 3
    private static final int      MAX_AGE_MONTHS               = 156;         // 156 -- 13 years (suggested 13 years)
    private static final double   CAERBANNOG_RATIO             = 1 / 8192.0; // 0.0001 (1 in 2^13) (added on top)
    private static final double   KIT_YEARLY_MORTALITY         = 0.75;        // 0.75
    private static final double[] YEARLY_MORTALITIES           = {
            // [0;1[ (will be ignored in static monthly mortality rate calculations)
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
    private static final double   KIT_MONTHLY_MORTALITY        =
            Math.pow((1 + KIT_YEARLY_MORTALITY), (1 / (Main.MONTHS_PER_YEAR * 1.0))) - 1;
    private static final double[] MONTHLY_MORTALITIES          = new double[MAX_AGE_MONTHS - Main.MONTHS_PER_YEAR];
    // ignoring year 0->1

    static
    {
        // calculating all constant mortality rates at compile time to improve performance
        int k = 0;
        for (double yearlyMortality : YEARLY_MORTALITIES)
        {
            for (int i = 0; i < Main.MONTHS_PER_YEAR; i++)
            {
                double m = (Math.pow((1 + yearlyMortality), (1 / (Main.MONTHS_PER_YEAR * 1.0))) - 1);
                MONTHLY_MORTALITIES[k] = m;
                k++;
            }
        }
    }

    /**
     * <p><strong>But!</strong>
     * <br>Follow only if ye be men of valor, for the entrance to this cave is guarded by a
     * creature so foul, so cruel that no man yet has fought with it <em>and lived</em>!
     * <br>Bones of full fifty men lie strewn about its lair!
     * <br>So, brave knights, if ye do doubt your courage or your strength, come nae further!
     * For death awaits you all <small>with nasty big pointy teeth</small>.</p>
     */
    private final boolean isRabbitOfCaerbannog;
    private final int     maturityStart;

    private boolean isMature    = false;
    private boolean isDead      = false;
    private int     ageInMonths = 0;

    Rabbit()
    {
        this.maturityStart = Main.MT.nextInt(INTERVAL_SIZE_MATURITY_START + 1) + EARLIEST_MATURITY_START;
        this.isRabbitOfCaerbannog = Main.MT.nextBoolean(CAERBANNOG_RATIO);
    }

    boolean isRabbitOfCaerbannog()
    {
        return this.isRabbitOfCaerbannog;
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    int getAgeInMonths()
    {
        return this.ageInMonths;
    }

    int getMaturityStart()
    {
        return this.maturityStart;
    }

    boolean isMature()
    {
        return this.isMature;
    }

    /**
     * <p>simply sets this {@link Rabbit}'s {@link #isMature} property to true, if it turns out to be of age and not mature yet.</p>
     */
    void maturificate()
    {
        if (this.isDead())
        {
            throw new RuntimeException("a dead rabbit doesn't need to mature: something went wrong");
        }
        //putting the least likely condition first, to get out of the AND conditional structure faster
        if ((this.getAgeInMonths() == this.getMaturityStart()) && !(this.isMature()))
        {
            this.isMature = true;
        }
    }

    /**
     * <p>simply sets this rabbit's {@link #isDead} property to true. Since the reverse should not be done, no need for a setter.</p>
     */
    final void kill()
    {
        if (!this.isDead())
        {
            this.isDead = true;
        }
    }

    /**
     * <p>examines whether a {@link Rabbit} should die, according to mortality rates and {@link #ageInMonths}, in which case it does</p>
     */
    private void checkDead()
    {
        double rdm = Main.MT.nextDouble();
        int    age = this.getAgeInMonths();
        //putting the likeliest candidates for death first, to get out of the OR conditional structure faster
        if
        (
                (
                        // death before 1 year: monthly mortality rate depends on maturity, which depends on individual
                        (age < Main.MONTHS_PER_YEAR)
                        &&
                        (
                                (this.isMature() && (rdm < MONTHLY_MORTALITIES[0])) // same rate as for 1-year-olds
                                ||
                                (rdm < KIT_MONTHLY_MORTALITY)
                        )
                )
                ||
                (
                        // death from other monthly mortality rates which were calculated at compile-time
                        (Main.MONTHS_PER_YEAR <= age && age < MAX_AGE_MONTHS)
                        &&
                        (rdm < MONTHLY_MORTALITIES[age - Main.MONTHS_PER_YEAR])
                )
                ||
                // death from old age
                (age == MAX_AGE_MONTHS)
        )
        {
            this.kill();
        }
    }

    /**
     * <p>increments a {@link Rabbit}'s {@link #ageInMonths} by one month, then calls checks for death and maturity</p>
     */
    void ageUp()
    {
        if (this.isDead())
        {
            throw new RuntimeException("a dead rabbit doesn't need to age: something went wrong");
        }
        this.ageInMonths++;
        this.checkDead();
        this.maturificate();
    }
}
