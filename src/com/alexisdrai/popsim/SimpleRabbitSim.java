package com.alexisdrai.popsim;

public class SimpleRabbitSim
{
    /**
     * <p>determines the size of a rabbit population, in couples, in function of how many months it has had to grow</p>
     *
     * @param months the amount of time since the first couple was spawned
     * @return the size of said rabbit population after said number of months, <strong>in couples</strong>
     */
    static long popByMonth(int months)
    {
        long m_1, m_2, current = 0;
        // step1: take a couple of baby rabbits
        // step2: wait a month, the couple is now mature for reproduction
        m_1 = m_2 = 1;
        if (months <= 0) throw new IllegalArgumentException("fibo(x) | x<=0 = N/A");
        if (months == 1 || months == 2)
        {
            System.out.println("Month: " + months + " | Pop: " + 1);
            return 1;
        }
        for (int i = 3; i <= months; i++)
        {
            // step3: apply the fibonacci formula
            current = m_1 + m_2;
            m_2 = m_1;
            m_1 = current;
        }
        // step4: display
        System.out.println("Month: " + months + " | Pop: " + current);
        return current;
    }
}
