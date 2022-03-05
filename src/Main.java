public class Main
{
    public static void main(String[] args)
    {

        // 1)

        long pop;
        for (int i = 1; i <= 240; i++)
        {
            pop = SimpleRabbitSim.popByMonth(i);
            if (pop > 8_000_000_000L)
            {
                System.out.println("\nOk, that's enough rabbits");
                break;
            }
        }

        //2)



    }
}
