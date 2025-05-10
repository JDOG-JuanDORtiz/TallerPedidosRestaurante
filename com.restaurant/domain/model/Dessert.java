package domain.model;

public class Dessert extends MenuItem {
    private boolean hasNuts;

    public Dessert(String name, double price, String description, boolean hasNuts) {
        super(name, price, "Dessert", description);
        this.hasNuts = hasNuts;
    }

    public boolean hasNuts() {
        return hasNuts;
    }

    public void setHasNuts(boolean hasNuts) {
        this.hasNuts = hasNuts;
    }
}
