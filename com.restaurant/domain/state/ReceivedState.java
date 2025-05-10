package domain.state;

import domain.model.Order;

public class ReceivedState implements OrderState {
    @Override
    public void nextState(Order order) {
        order.setState(new PreparingState());
    }

    @Override
    public String getStateName() {
        return "Received";
    }
}
