enum Sex {
    FEMALE,
    MALE
}

public class Rabbit {
    private final short MAX_AGE_MONTHS = 240;
    private final Sex sex;
    private final int fertilityStart;
    private boolean isFertile;
    private short ageMonths;
    private boolean isDead;
    private boolean isDue;

    Rabbit(Sex s) {
        this.sex = s;
        this.fertilityStart = 0; // rdm in [5;8] _normal + rdm 10% infertile females _continuous
        this.isFertile = false;
        this.ageMonths = 0;
        this.isDead = false;
        this.isDue = false;
    }

    Rabbit() {
        this.sex = Sex.FEMALE; // rdm 50/50 _continuous
        this.fertilityStart = 0; // rdm in [5;8] _normal + rdm 10% infertile females _continuous
        this.isFertile = false;
        this.ageMonths = 0;
        this.isDead = false;
        this.isDue = false;
    }

    public boolean isFertile() {
        return isFertile;
    }

    public void setFertile(boolean fertile) {
        isFertile = fertile;
    }

    public int getFertilityStart() {
        return fertilityStart;
    }

    public short getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(short ageMonths) {
        this.ageMonths = ageMonths;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public Sex getSex() {
        return sex;
    }

    public boolean isDue() {
        return isDue;
    }

    public void setDue(boolean due) {
        isDue = due;
    }

    void ageUp() {
        this.ageMonths++;
        int rdm = 1; //rdm [0;1] _continuous

        if (this.ageMonths == MAX_AGE_MONTHS
        || rdm < 1) { //see notes, rdm hardcoded
            this.setDead(true);
            return;
        }
        if (this.ageMonths == this.fertilityStart) {
            this.mature();
        }


    }

    private void mature() {
        if (!(this.isFertile)) {
            this.setFertile(true);
        }
    }

}
