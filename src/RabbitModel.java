import java.util.ArrayList;

public class RabbitModel
{
    private ArrayList<Rabbit> maleRabbits;
    private ArrayList<Rabbit> femaleRabbits;
    private static final int MONTHS = 24;

    public RabbitModel(int numFem, int numMal)
    {
        this.femaleRabbits = new ArrayList<>();
        this.maleRabbits = new ArrayList<>();
        for (int i = 0; i < numFem; i++)
        {
            femaleRabbits.add(new Rabbit(Sex.FEMALE));
        }
        for (int i = 0; i < numMal; i++)
        {
            maleRabbits.add(new Rabbit(Sex.MALE));
        }
    }

    private void step(Rabbit rabbit)
    {
        rabbit.ageUp();
        if (rabbit.isDead())
        {
            this.femaleRabbits.remove(rabbit);
        }
    }

    void run()
    {
        for (int i = MONTHS; i >= 0; i--)
        {
            for (Rabbit doe : this.femaleRabbits)
            {
                this.step(doe);
            }
            for (Rabbit buck : this.maleRabbits)
            {
                this.step(buck);
            }
        }
    }

    private void giveBirth()
    {
        int n = 4; // rdm [2;6] _normal
        for (Rabbit doe : this.femaleRabbits)
        {
            for (int i = 0; i < n; i++)
            {
                if (doe.isFertile() && doe.isDue())
                {

                }
            }
        }
    }
}
