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
                System.out.println("\nOk, that's enough rabbits\n");
                break;
            }
        }

        // 2)

        for (int i = 1; i <= 50; i++)
        {
            RabbitModel model = new RabbitModel(7, 3);
            model.run(i);
        }

    }

}
