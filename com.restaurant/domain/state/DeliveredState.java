package domain.state;

import domain.model.Order;

public class DeliveredState implements OrderState {
    @Override
    public void nextState(Order order) {
        // Terminal state, no next state
        System.out.println("Order is already delivered. No further state transitions.");
    }

    @Override
    public String getStateName() {
        return "Delivered";
    }
}
