package domain.state;

import domain.model.Order;

public interface OrderState {
    void nextState(Order order);
    String getStateName();
}
