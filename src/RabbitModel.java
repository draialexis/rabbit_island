import java.util.ArrayList;
import java.util.Iterator;

public class RabbitModel
{
    private final ArrayList<Rabbit> rabbits;
    private static final int MONTHS = 30;

    public RabbitModel(int numFem, int numMal)
    {
        this.rabbits = new ArrayList<>();
        for (int i = 0; i < numFem; i++)
        {
            this.rabbits.add(new Rabbit(Sex.FEMALE));
        }
        for (int i = 0; i < numMal; i++)
        {
            this.rabbits.add(new Rabbit(Sex.MALE));
        }
    }

    void run()
    {
        for (int i = MONTHS; i > 0; i--)
        {
            Iterator<Rabbit> itr = this.rabbits.iterator();
            while (itr.hasNext())
            {
                Rabbit rabbit = itr.next();
                rabbit.ageUp();
                if (rabbit.isDead())
                {
                    itr.remove();
                }
                if (rabbit.isDue()) // not taking the presence of males into account, to improve performance
                {
                    this.giveBirth();
                }
            }
            System.out.println(this.rabbits.size());
        }
    }

    private void giveBirth()
    {
        double f = Math.random() / Math.nextDown(1.0);
        int n = (int) (2 * (1.0 - f) + 7 * f);
        // TODO rdm in [2;6] _normal
        for (Rabbit rabbit : this.rabbits)
        {
            if (rabbit.isDue())
            {
                if (rabbit.getSex() == Sex.MALE || !rabbit.isFertile())
                {
                    throw new RuntimeException("wuh-oh, infertile or male rabbit giving birth");
                }
                for (int i = 0; i < n; i++)
                {
                    rabbits.add(new Rabbit());
                }
            }
        }
    }
}
