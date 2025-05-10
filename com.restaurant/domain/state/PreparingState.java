package domain.state;

import domain.model.Order;

public class PreparingState implements OrderState {
    @Override
    public void nextState(Order order) {
        order.setState(new ReadyState());
    }

    @Override
    public String getStateName() {
        return "Preparing";
    }
}
