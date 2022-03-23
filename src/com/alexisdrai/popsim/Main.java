package com.alexisdrai.popsim;

import com.alexisdrai.util.FileStuff;
import com.alexisdrai.util.MersenneTwisterFast;

public final class Main
{
    public static final MersenneTwisterFast MT;

    static
    {
        // initializing the Mersenne Twister
        MT = new MersenneTwisterFast(new int[]{0x123, 0x234, 0x345, 0x456});
    }

    public static final int MONTHS_PER_YEAR = 12;

    public static final int YEARS_PER_EXPERIMENT = 20;
    public static final int TOTAL_MONTHS         = MONTHS_PER_YEAR * YEARS_PER_EXPERIMENT;

    public static void main(String[] args)
    {

        System.out.println("================= 1) Simple rabbit pop sim (fibo) =================");
        long pop = 0;
        int  i   = 0;
        while (pop < 8_000_000_000L)
        {
            pop = SimpleRabbitSim.popByMonth(++i);
        }
        System.out.println("\nOk, that's enough rabbits\n");

        System.out.println("================= 2) Dank ill rabbit pop sim (actual sim) =================");
        final int    REPLICATES = 2000; // STUDENT_T depends on this
        final double STUDENT_T  = 2.5759; // depends on REPLICATES
        // refer to https://www.supagro.fr/cnam-lr/statnet/tables.htm for co-dependant values
        System.out.println("launching " + REPLICATES + " replicates");

        final String FILENAME = "data_results/rabbit_pop_results.txt";

        final int MALES   = 5;
        final int FEMALES = 10;

        double mean     = 0;
        double variance = 0;
        double stdDeviation;
        double stdError;
        double errorMargin;
        int[]  results  = new int[REPLICATES];

        int tmp;

        // logging results and calculating estimated mean
        FileStuff.createFile(FILENAME);
        for (i = 0; i < REPLICATES; i++)
        {
            RabbitModel model = new RabbitModel(FEMALES, MALES);
            // getting final pop results
            tmp = model.run();
            mean += tmp;
            results[i] = tmp;
            FileStuff.writeToFile(FILENAME, tmp + ",");
        }
        mean /= REPLICATES;

        // calculating the variance using previous results
        for (i = 0; i < REPLICATES; i++)
        {
            variance += Math.pow((results[i] - mean), 2);
        }
        variance /= REPLICATES - 1;

        // doing the math on confidence interval
        stdDeviation = Math.sqrt(variance);
        stdError = stdDeviation / Math.sqrt(REPLICATES);
        errorMargin = STUDENT_T * stdError;

        final String printout = "\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
                                "after " + REPLICATES + " replicates of a " + YEARS_PER_EXPERIMENT +
                                "-year-long experiment\n" +
                                "with " + FEMALES + " female and " + MALES + " male starting rabbits\n" +
                                "observed population levels were such:\n" +
                                "mean = " + mean + "\n" +
                                "variance = " + variance + "\n" +
                                "standard deviation = " + stdDeviation + "\n" +
                                "standard error = " + stdError + "\n" +
                                "margin of error = " + errorMargin + "\n" +
                                "99% confidence interval = [" + (mean - errorMargin) + "," + (mean + errorMargin) + "]";

        // logging and printing out results
        FileStuff.writeToFile(FILENAME, printout);
        System.out.println(printout);
    }
}