package no.pew.crabi.orderbook;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Order {
  private final String id;
  private final OffsetDateTime time;
  private final Limit limit;
  private final BigDecimal volume;

  public Order(String id, OffsetDateTime time, Limit limit, BigDecimal volume) {
    this.id = id;
    this.time = time;
    this.limit = limit;
    this.volume = volume;
  }

  public String getId() {
    return id;
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public Limit getLimit() {
    return limit;
  }

  public BigDecimal getVolume() {
    return volume;
  }
}
