package no.pew.crabi.orderbook.events;

import no.pew.crabi.orderbook.events.Side;

import java.math.BigDecimal;

public class OrderRemoveEvent {
  private final String id;

  public OrderRemoveEvent(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
