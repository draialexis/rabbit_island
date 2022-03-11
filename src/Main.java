import java.util.Arrays;
import java.util.OptionalLong;

public class Main
{
    public static void main(String[] args)
    {

        System.out.println("///// 1) Simple rabbit pop sim /////");

        long pop;
        for (int i = 1; i <= 240; i++)
        {
            pop = SimpleRabbitSim.popByMonth(i);
            if (pop > 8_000_000_000L)
            {
                System.out.println("\nOk, that's enough rabbits\n");
                break;
            }
        }

        System.out.println("///// 2) Dank ill rabbit pop sim /////");

        final String fileName = "rabbit_pop_results.txt";

        final int    MALES      = 10;
        final int    FEMALES    = 20;
        final int    YEARS      = 20;
        final int    REPLICATES = 101; // DO NOT CHANGE & depends on STUDENT_T
        final double STUDENT_T  = 2.6259; // DO NOT CHANGE & depends on REPLICATES
        // for alpha=0.01, at n=101: n-1=100 --> t=2.6259
        // https://www.supagro.fr/cnam-lr/statnet/tables.htm

        double mean     = 0;
        double variance = 0;
        double confRad;
        long[] results  = new long[REPLICATES];
        long   result;

        FileStuff.createFile(fileName);
        for (int i = 0; i < REPLICATES; i++)
        {
            RabbitModel model = new RabbitModel(FEMALES, MALES);

            result = model.run(i + 1, YEARS);

            FileStuff.writeToFile(fileName, Long.toString(result));
            results[i] = result;
            mean += result;

            // TODO calculate variance, mean, confidence interval etc. for final pops
        }

        mean /= REPLICATES;
        OptionalLong max = Arrays.stream(results).max();
        OptionalLong min = Arrays.stream(results).min();
        for (int i = 0; i < REPLICATES; i++)
        {
            variance += Math.pow((results[i] - mean), 2);
        }
        variance /= (REPLICATES - 1);

        confRad = STUDENT_T * Math.sqrt(variance / REPLICATES); // -1 <= 0-indexed, -1 <= freedom

        final String printout = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
                                "after " + REPLICATES + " replicates of a " + YEARS + "-year-long experiment\n" +
                                "with " + FEMALES + " female and " + MALES + " male starting rabbits\n" +
                                "observed population levels were such:\n" +
                                "min = " + min + "\n" +
                                "max = " + max + "\n" +
                                "mean = " + mean + "\n" +
                                "variance = " + variance + "\n" +
                                "confidence radius = [" + (mean - confRad) + "," + (mean + confRad) + "]\n" +
                                "with an error margin of " + 0.01;

        FileStuff.writeToFile(fileName, printout);

        System.out.println(printout);

        // TODO doc
        // TODO add exception handling and validation
    }

}
