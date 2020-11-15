package no.pew.crabi.orderbook;

import no.pew.crabi.orderbook.events.Side;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class Limit {
  private final BigDecimal price;
  private final Side side;
  private final HashMap<String, Order> orders = new HashMap<>();
  private BigDecimal totalVolume;

  public Limit(BigDecimal price, Side side) {
    this.price = price;
    this.side = side;
    this.totalVolume = BigDecimal.ZERO;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Side getSide() {
    return side;
  }

  public BigDecimal getTotalVolume() {
    return totalVolume;
  }

  public Order addOrder(Order order) {
    totalVolume = totalVolume.add(order.getVolume(), MathContext.DECIMAL64);
    orders.put(order.getId(), order);
    return order;
  }

  public void removeOrder(String id) {
    Order order = orders.remove(id);
    if (order == null) return;
    totalVolume = totalVolume.subtract(order.getVolume(), MathContext.DECIMAL64);
  }

  public boolean isEmpty() {
    return orders.isEmpty();
  }
}
