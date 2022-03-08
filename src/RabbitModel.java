import java.util.ArrayList;

public class RabbitModel {
    private ArrayList<Rabbit> maleRabbits;
    private ArrayList<Rabbit> femaleRabbits;
    private static final int MONTHS = 240;
    private static double[] yearlyMortality = new double[]{
            0.5,
            0.25,
            0.25,
            0.25,
            0.25,
            0.25,
            0.25,
            0.25,
            0.4,
            0.55,
            0.7,
            0.85,
            1
    };

    private double[] monthlyMortality = new double[MONTHS];

    public RabbitModel(int numFem, int numMal) {
        int n = yearlyMortality.length;
        for (double v : yearlyMortality) {
            for (int j = 0; j < 12; j++) {
                monthlyMortality[j] = Math.pow((1 + v),(1 / 12.0)) - 1;
            }
        }
        this.femaleRabbits = new ArrayList<>();
        femaleRabbits.add(numFem, new Rabbit(Sex.FEMALE));
        this.maleRabbits = new ArrayList<>();
        maleRabbits.add(numMal, new Rabbit(Sex.MALE));
    }

    void run() {
        for (int i = MONTHS; i >= 0; i--) {
            for (Rabbit doe : this.femaleRabbits) {
                doe.ageUp();
            }
            for (Rabbit buck : this.maleRabbits) {
                buck.ageUp();
            }
        }
    }

    private void giveBirth() {
        int n = 4; // rdm [2;6] _normal
        for (Rabbit doe : this.femaleRabbits) {
            for (int i = 0; i < n; i++) {
                if (doe.isFertile() && doe.isDue()) {

                }
            }
        }
    }
}
