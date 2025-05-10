package domain.state;

import domain.model.Order;

public class ReadyState implements OrderState {
    @Override
    public void nextState(Order order) {
        order.setState(new DeliveredState());
    }

    @Override
    public String getStateName() {
        return "Ready";
    }
}
