public class Main
{
    static final MersenneTwisterFast MT;
    static final int                 MAX_INT = 2147483647;

    static
    {
        // initializing the Mersenne Twister
        MT = new MersenneTwisterFast(new int[]{0x123, 0x234, 0x345, 0x456});
    }

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

        final int    REPLICATES = 50; // DO NOT CHANGE, depends on STUDENT_T
        final double STUDENT_T  = 2.68; // DO NOT CHANGE, depends on REPLICATES
        // refer to https://www.supagro.fr/cnam-lr/statnet/tables.htm for co-dependant values

        final String fileName = "rabbit_pop_results.txt";

        final int MALES   = 5;
        final int FEMALES = 10;
        final int MONTHS  = 156; // increase at your own risks

        double mean     = 0;
        double variance = 0;
        double stdDeviation;
        double stdError;
        double errorMargin;
        long[] results  = new long[REPLICATES];
        long   result;

        FileStuff.createFile(fileName);
        for (i = 0; i < REPLICATES; i++)
        {
            RabbitModel model = new RabbitModel(FEMALES, MALES);

            result = model.run(MONTHS);

            FileStuff.writeToFile(fileName, result + ",");
            results[i] = result;
            mean += result;
        }

        mean /= REPLICATES;

        for (i = 0; i < REPLICATES; i++)
        {
            variance += Math.pow((results[i] - mean), 2);
        }

        variance /= REPLICATES - 1;
        stdDeviation = Math.sqrt(variance);
        stdError = stdDeviation / Math.sqrt(REPLICATES);
        errorMargin = STUDENT_T * stdError;

        final String printout = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
                                "after " + REPLICATES + " replicates of a " + (MONTHS / 12.0) +
                                "-year-long experiment\n" +
                                "with " + FEMALES + " female and " + MALES + " male starting rabbits\n" +
                                "observed population levels were such:\n" +
                                "mean = " + mean + "\n" +
                                "variance = " + variance + "\n" +
                                "standard deviation = " + stdDeviation + "\n" +
                                "standard error = " + stdError + "\n" +
                                "margin of error = " + errorMargin + "\n" +
                                "99% confidence interval = [" + (mean - errorMargin) + "," + (mean + errorMargin) + "]";

        FileStuff.writeToFile(fileName, printout);

        System.out.println(printout);

        // TODO doc
        // TODO add exception handling and validation
    }

}
