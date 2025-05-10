package domain.model;

public class Appetizer extends MenuItem {
    private boolean isVegetarian;

    public Appetizer(String name, double price, String description, boolean isVegetarian) {
        super(name, price, "Appetizer", description);
        this.isVegetarian = isVegetarian;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }
}
