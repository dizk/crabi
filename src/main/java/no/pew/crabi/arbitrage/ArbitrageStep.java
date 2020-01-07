package no.pew.crabi.arbitrage;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ArbitrageStep {

    private final LimitOrder order;

    public ArbitrageStep(LimitOrder order) {
        this.order = order;
    }

    public String getFrom() {
        if (order.getType().equals(Order.OrderType.BID)) {
            return order.getCurrencyPair().counter.getCurrencyCode();
        } else {
            return order.getCurrencyPair().base.getCurrencyCode();
        }
    }

    public String getTo() {
        if (order.getType().equals(Order.OrderType.BID)) {
            return order.getCurrencyPair().base.getCurrencyCode();
        } else {
            return order.getCurrencyPair().counter.getCurrencyCode();
        }
    }

    public BigDecimal getRate() {
        if (order.getType().equals(Order.OrderType.BID)) {
            return order.getLimitPrice();
        } else {
            // ASK == inverse rate
            return BigDecimal.ONE.divide(order.getLimitPrice(), 10, RoundingMode.HALF_DOWN);
        }
    }

    public BigDecimal getPrice() {
        return order.getLimitPrice();
    }

    public BigDecimal volume() {
        return order.getRemainingAmount();
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "ArbitrageStep{" +
                "order=" + order +
                '}';
    }
}
