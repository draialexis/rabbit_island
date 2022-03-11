public class Main
{
    public static void main(String[] args)
    {

        //        System.out.println("///// 1) Simple rabbit pop sim /////");
        //
        //        long pop;
        //        for (int i = 1; i <= 240; i++)
        //        {
        //            pop = SimpleRabbitSim.popByMonth(i);
        //            if (pop > 8_000_000_000L)
        //            {
        //                System.out.println("\nOk, that's enough rabbits\n");
        //                break;
        //            }
        //        }

        System.out.println("///// 2) Dank ill rabbit pop sim /////");

        for (int i = 1; i <= 50; i++)
        {
            RabbitModel model = new RabbitModel(5, 5);
            model.run(i, 20);
        }
        //TODO doc
    }

}
