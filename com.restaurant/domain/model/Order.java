package domain.model;

import domain.service.discount.DiscountStrategy;
import domain.state.OrderState;
import domain.state.ReceivedState;
import domain.observer.OrderObserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order {
    private String id;
    private Customer customer;
    private List<OrderItem> items;
    private Date dateCreated;
    private OrderState state;
    private List<OrderObserver> observers;
    private DiscountStrategy discountStrategy;
    private final double TAX_RATE = 0.08; // 8% tax

    public Order(Customer customer) {
        this.id = UUID.randomUUID().toString();
        this.customer = customer;
        this.items = new ArrayList<>();
        this.dateCreated = new Date();
        this.state = new ReceivedState();
        this.observers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(MenuItem menuItem, int quantity) {
        OrderItem item = new OrderItem(menuItem, quantity);
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
        notifyObservers();
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(this);
        }
    }

    public void nextState() {
        state.nextState(this);
    }

    public String getStatus() {
        return state.getStateName();
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double calculateTax() {
        return calculateSubtotal() * TAX_RATE;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double discountedSubtotal = (discountStrategy != null) ? 
                discountStrategy.applyDiscount(subtotal) : subtotal;
        double tax = discountedSubtotal * TAX_RATE;
        return discountedSubtotal + tax;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(id.substring(0, 8)).append("\n");
        sb.append("Customer: ").append(customer.getName()).append("\n");
        sb.append("Status: ").append(getStatus()).append("\n");
        sb.append("Items:\n");
        
        for (OrderItem item : items) {
            sb.append("  ").append(item).append("\n");
        }
        
        sb.append("Subtotal: $").append(String.format("%.2f", calculateSubtotal())).append("\n");
        
        if (discountStrategy != null) {
            double discount = calculateSubtotal() - discountStrategy.applyDiscount(calculateSubtotal());
            sb.append("Discount: -$").append(String.format("%.2f", discount)).append("\n");
        }
        
        sb.append("Tax: $").append(String.format("%.2f", calculateTax())).append("\n");
        sb.append("Total: $").append(String.format("%.2f", calculateTotal())).append("\n");
        
        return sb.toString();
    }
}