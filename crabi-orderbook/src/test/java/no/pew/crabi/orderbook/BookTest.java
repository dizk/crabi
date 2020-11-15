package no.pew.crabi.orderbook;

import no.pew.crabi.orderbook.events.OrderEntryEvent;
import no.pew.crabi.orderbook.events.OrderRemoveEvent;
import no.pew.crabi.orderbook.events.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

  @Test
  void sellOrder() {
    BookImpl book = new BookImpl();

    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.SELL, BigDecimal.ONE, BigDecimal.ONE));

    Limit limit = book.getBestLimit(Side.SELL);

    assertThat(limit.getPrice()).isEqualByComparingTo("1");
  }

  @Test
  void buyOrder() {
    BookImpl book = new BookImpl();

    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.BUY, BigDecimal.ONE, BigDecimal.ONE));

    Limit limit = book.getBestLimit(Side.BUY);

    assertThat(limit).isNotNull();
    assertThat(limit.getPrice()).isEqualByComparingTo("1");
  }

  @Test
  void multipleSellOrders() {
    BookImpl book = new BookImpl();

    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.SELL, BigDecimal.ONE, BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.SELL, new BigDecimal("1.0001"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.SELL, new BigDecimal("1.1"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.SELL, new BigDecimal("1.2"), BigDecimal.ONE));

    Limit limit = book.getBestLimit(Side.SELL);

    assertThat(limit.getPrice()).isEqualByComparingTo("1");
  }

  @Test
  void multipleBuyOrders() {
    BookImpl book = new BookImpl();

    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.BUY, BigDecimal.ONE, BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.BUY, new BigDecimal("1.0001"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.BUY, new BigDecimal("1.1"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent(
            UUID.randomUUID().toString(), Side.BUY, new BigDecimal("1.2"), BigDecimal.ONE));

    Limit limit = book.getBestLimit(Side.BUY);

    assertThat(limit.getPrice()).isEqualByComparingTo("1.2");
  }

  @Test
  void orderRemove() {
    BookImpl book = new BookImpl();

    book.handleOrderEntryEvent(new OrderEntryEvent("1", Side.BUY, new BigDecimal("1"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent("2", Side.BUY, new BigDecimal("2"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent("3", Side.BUY, new BigDecimal("3"), BigDecimal.ONE));
    book.handleOrderEntryEvent(
        new OrderEntryEvent("4", Side.BUY, new BigDecimal("4"), BigDecimal.ONE));

    assertThat(book.getBestLimit(Side.BUY).getPrice()).isEqualByComparingTo("4");

    book.handleOrderRemoveEvent(new OrderRemoveEvent("4"));
    assertThat(book.getBestLimit(Side.BUY).getPrice()).isEqualByComparingTo("3");

    book.handleOrderRemoveEvent(new OrderRemoveEvent("1"));
    assertThat(book.getBestLimit(Side.BUY).getPrice()).isEqualByComparingTo("3");
  }
}
