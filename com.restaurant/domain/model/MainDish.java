package domain.model;

public class MainDish extends MenuItem {
    private boolean isSpicy;

    public MainDish(String name, double price, String description, boolean isSpicy) {
        super(name, price, "Main Dish", description);
        this.isSpicy = isSpicy;
    }

    public boolean isSpicy() {
        return isSpicy;
    }

    public void setSpicy(boolean spicy) {
        isSpicy = spicy;
    }
}
