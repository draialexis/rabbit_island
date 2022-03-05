public class SimpleRabbitSim
{
    /**
     * popByMonth
     * determines the size of a rabbit population, <strong>in couples</strong>,
     * with regard to how many months it has had to grow
     *
     * @param months the amount of time since the first couple was spawned
     * @return the size of said rabbit population after said number of months, <strong>in couples</strong>
     */
    static long popByMonth(int months)
    {
        long m_1, m_2, current = 0;
        int i;
        // step1: take a couple of baby rabbits
        m_1 = 1;
        // step2: wait a month, the couple is now mature for reproduction
        m_2 = 1;
        if (months <= 0) throw new IllegalArgumentException("fibo(x) | x<=0 = N/A");
        if (months == 1 || months == 2)
        {
            System.out.println("Month: " + months + " | Pop: " + 1);
            // System.out.print(1 + ", ");
            return 1;
        }
        for (i = 3; i <= months; i++)
        {
            // step3: do the math
            current = m_1 + m_2;
            m_2 = m_1;
            m_1 = current;
        }
        // step4: display
        System.out.println("Month: " + months + " | Pop: " + current);
        // System.out.print(current + ", ");
        return current;
    }
}
