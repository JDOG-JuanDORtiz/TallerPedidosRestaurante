package domain.model;

public class Beverage extends MenuItem {
    private boolean isAlcoholic;

    public Beverage(String name, double price, String description, boolean isAlcoholic) {
        super(name, price, "Beverage", description);
        this.isAlcoholic = isAlcoholic;
    }

    public boolean isAlcoholic() {
        return isAlcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        isAlcoholic = alcoholic;
    }
}
