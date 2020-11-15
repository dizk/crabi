package no.pew.crabi.orderbook;

import no.pew.crabi.orderbook.events.OrderEntryEvent;
import no.pew.crabi.orderbook.events.OrderRemoveEvent;
import no.pew.crabi.orderbook.events.Side;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static no.pew.crabi.orderbook.events.Side.SELL;

public class BookImpl implements Book {
  private final NavigableMap<BigDecimal, Limit> buyLimits =
      new TreeMap<>(Comparator.reverseOrder());
  private final NavigableMap<BigDecimal, Limit> sellLimits =
      new TreeMap<>(Comparator.naturalOrder());
  private final Map<String, Limit> orderIdToLimit = new HashMap<>();

  public void handleOrderEntryEvent(OrderEntryEvent orderEntryEvent) {
    Limit limit = addLimitIfNotExists(getLimitMap(orderEntryEvent.getSide()), orderEntryEvent);
    Order order =
        limit.addOrder(
            new Order(
                orderEntryEvent.getId(), OffsetDateTime.now(), limit, orderEntryEvent.getVolume()));
    orderIdToLimit.put(order.getId(), limit);
  }

  public void handleOrderRemoveEvent(OrderRemoveEvent orderRemoveEvent) {
    Limit limit = orderIdToLimit.remove(orderRemoveEvent.getId());
    if (limit == null) throw new RuntimeException("order id not found");
    limit.removeOrder(orderRemoveEvent.getId());
    if (limit.isEmpty()) {
      getLimitMap(limit.getSide()).remove(limit.getPrice());
    }
  }

  private Limit addLimitIfNotExists(
      Map<BigDecimal, Limit> limitMap, OrderEntryEvent orderEntryEvent) {
    return limitMap.computeIfAbsent(
        orderEntryEvent.getPrice(),
        key -> new Limit(orderEntryEvent.getPrice(), orderEntryEvent.getSide()));
  }

  public Limit getBestLimit(Side side) {
    Map.Entry<BigDecimal, Limit> entry = getLimitMap(side).firstEntry();
    if (entry == null) return null;
    return entry.getValue();
  }

  private NavigableMap<BigDecimal, Limit> getLimitMap(Side side) {
    if (SELL.equals(side)) {
      return sellLimits;
    } else {
      return buyLimits;
    }
  }
}
