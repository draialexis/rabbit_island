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

    /*
    If we want to keep our for-each loop, then we can. It's just that we need to wait until after iterating before we remove the elements. Let's try this out by adding what we want to remove to a toRemove list as we iterate:

    List<Integer> integers = newArrayList(1, 2, 3);
    List<Integer> toRemove = newArrayList();

    for (Integer integer : integers) {
        if(integer == 2) {
            toRemove.add(integer);
        }
    }
    integers.removeAll(toRemove);

    assertThat(integers).containsExactly(1, 3);
    This is another effective way of getting around the problem.

     */
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
                    continue;
                }

                this.giveBirth();
            }
            System.out.println(this.rabbits.size());
        }
    }

    /*

    If we want to keep our for-each loop, then we can. It's just that we need to wait until after iterating before we remove the elements. Let's try this out by adding what we want to remove to a toRemove list as we iterate:

List<Integer> integers = newArrayList(1, 2, 3);
List<Integer> toRemove = newArrayList();

for (Integer integer : integers) {
    if(integer == 2) {
        toRemove.add(integer);
    }
}
integers.removeAll(toRemove);

assertThat(integers).containsExactly(1, 3);
This is another effective way of getting around the problem.

     */
    private void giveBirth()
    {

        Iterator<Rabbit> itr = this.rabbits.iterator();
        while (itr.hasNext())
        {
            Rabbit rabbit = itr.next();
            int littersDue = rabbit.getYearlyDue();
            for (int i = 0; i < littersDue; i++)
            // not taking the presence of males into account, to improve performance
            {
                if (Math.random() < 0.15)
                {
                    rabbit.kill();
                    itr.remove();
                    continue;
                } else
                {
                    double f;
                    int n;
                    f = Math.random() / Math.nextDown(1.0);
                    n = (int) (2 * (1.0 - f) + 7 * f);
                    System.out.println("giving birth to " + n);
                    // TODO rdm in [2;6] _normal
                    if (rabbit.getSex() == Sex.FEMALE && rabbit.isFertile())
                    {
                        for (int j = 0; j < n; j++)
                        {
                            rabbits.add(new Rabbit());
                        }
                    }

                }
                System.out.println(this.rabbits.size());
                rabbit.resetYearlyDue();
                // TODO improve that to prevent inaccuracy (use array as litter planner?)
                // as it stands, a female that is due x litters in the next 12 months will spawn them all
                // even if she should die the following month
            }
        }
    }
}
