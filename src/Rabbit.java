import java.util.HashMap;
import java.util.Map;

enum Sex
{
    FEMALE, MALE
}

public class Rabbit
{
    private final short MAX_AGE_MONTHS = 156;
    private final Sex sex;
    private final int fertilityStart;
    private boolean isFertile;
    private short ageMonths;
    private double monthlyMortality;
    private boolean isDead;
    private boolean isMature;
    private boolean isDue;
    private static Map<Character, Double> yearlyMortality;

    static
    {
        yearlyMortality = new HashMap<>();
        yearlyMortality.put('k', 0.98);  // kid 0.5
        yearlyMortality.put('m', 0.25); // mature
        yearlyMortality.put('1', 0.4);  // mature + 1 ([8,  9 [ yo)
        yearlyMortality.put('2', 0.55); // mature + 2 ([9,  10[ yo)
        yearlyMortality.put('3', 0.7);  // mature + 3 ([10, 11[ yo)
        yearlyMortality.put('4', 0.85); // mature + 4 ([11, 12[ yo)
        yearlyMortality.put('5', 1.0);  // mature + 5 ([12, 13[ yo)
    }

    Rabbit()
    {
        this(Sex.FEMALE); // rdm 50/50 _continuous
    }

    Rabbit(Sex s)
    {
        double f = Math.random() / Math.nextDown(1.0);
        this.sex = s;
        this.fertilityStart = (int) (5 * (1.0 - f) + 9 * f);
        // TODO rdm in [5;8] _continuous + rdm 10% infertile females _continuous
        this.isFertile = false;
        this.ageMonths = 0;
        this.isDead = false;
        this.isMature = false;
        this.isDue = false;
    }

    public boolean isFertile()
    {
        return this.isFertile;
    }

    public void setFertile(boolean fertile)
    {
        this.isFertile = fertile;
    }

    public int getFertilityStart()
    {
        return this.fertilityStart;
    }

    public short getAgeMonths()
    {
        return this.ageMonths;
    }

    public void setAgeMonths(short ageMonths)
    {
        this.ageMonths = ageMonths;
    }

    double getMonthlyMortality()
    {
        return this.monthlyMortality;
    }

    void setMonthlyMortality(double monthlyMortality)
    {
        this.monthlyMortality = monthlyMortality;
    }

    void updateMonthlyMortality()
    {
        for (int i = 1; i <= MAX_AGE_MONTHS; i++)
        {
            char ch = 0;
            if (!(this.isMature()))
            {
                ch = 'k';
            } else if (i <= 84)
            {
                ch = 'm';
            } else if (i <= 96)
            {
                ch = '1';
            } else if (i <= 108)
            {
                ch = '2';
            } else if (i <= 120)
            {
                ch = '3';
            } else if (i <= 132)
            {
                ch = '4';
            } else if (i <= 144)
            {
                ch = '5';
            }
            if (ch == 0)
            {
                throw new RuntimeException("could not establish rabbit monthly mortality rate");
            } else
            {
                double m_m = Math.pow((1 + yearlyMortality.get(ch)), (1 / 12.0)) - 1;
                System.out.println(this + " ; age = " + this.ageMonths + " ; m_m = " + m_m);
                this.setMonthlyMortality(m_m);
            }
        }
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    public void setDead(boolean dead)
    {
        this.isDead = dead;
    }

    boolean isMature()
    {
        return this.isMature;
    }

    void setMature(boolean mature)
    {
        this.isMature = mature;
    }

    public Sex getSex()
    {
        return this.sex;
    }

    public boolean isDue()
    {
        return this.isDue;
    }

    public void setDue(boolean due)
    {
        this.isDue = due;
    }

    void ageUp()
    {
        this.ageMonths++;
        double rdm = Math.random(); //rdm [0;1] _continuous
        // TODO get better PRNG

        if (this.ageMonths == this.fertilityStart)
        {
            this.mature();
        }
        this.updateMonthlyMortality();
        if (this.ageMonths == MAX_AGE_MONTHS || rdm < this.monthlyMortality)
        { //see notes, rdm hardcoded
            this.setDead(true);
        }

    }

    private void mature()
    {
        if (!(this.isFertile))
        {
            this.setFertile(true);
        }
    }

//    @Override
//    public String toString()
//    {
//        return "Rabbit{" + "MAX_AGE_MONTHS=" + MAX_AGE_MONTHS + ", sex=" + sex + ", fertilityStart=" + fertilityStart +
//               ", isFertile=" + isFertile + ", ageMonths=" + ageMonths + ", monthlyMortality=" + monthlyMortality +
//               ", isDead=" + isDead + ", isMature=" + isMature + ", isDue=" + isDue + '}';
//    }
}
