package no.pew.crabi.orderbook;

import no.pew.crabi.orderbook.events.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LimitTest {
  @Test
  void totalVolumeIncreases() {
    Limit limit = new Limit(BigDecimal.ZERO, Side.SELL);

    limit.addOrder(
        new Order(UUID.randomUUID().toString(), OffsetDateTime.now(), limit, BigDecimal.ONE));
    limit.addOrder(
        new Order(UUID.randomUUID().toString(), OffsetDateTime.now(), limit, BigDecimal.ONE));
    limit.addOrder(
        new Order(UUID.randomUUID().toString(), OffsetDateTime.now(), limit, BigDecimal.ONE));
    limit.addOrder(
        new Order(UUID.randomUUID().toString(), OffsetDateTime.now(), limit, BigDecimal.ONE));

    assertThat(limit.getTotalVolume()).isEqualByComparingTo("4");
  }

  @Test
  void addAndRemoveOrderTotalVolume() {
    Limit limit = new Limit(BigDecimal.ZERO, Side.SELL);

    limit.addOrder(new Order("1", OffsetDateTime.now(), limit, BigDecimal.ONE));
    limit.addOrder(new Order("2", OffsetDateTime.now(), limit, BigDecimal.ONE));
    limit.addOrder(new Order("3", OffsetDateTime.now(), limit, BigDecimal.ONE));
    assertThat(limit.getTotalVolume()).isEqualByComparingTo("3");

    limit.removeOrder("2");
    assertThat(limit.getTotalVolume()).isEqualByComparingTo("2");
  }

  @Test
  void emptyLimit() {
    Limit limit = new Limit(BigDecimal.ZERO, Side.SELL);
    assertThat(limit.isEmpty()).isTrue();

    limit.addOrder(new Order("1", OffsetDateTime.now(), limit, BigDecimal.ONE));
    assertThat(limit.isEmpty()).isFalse();

    limit.removeOrder("1");
    assertThat(limit.isEmpty()).isTrue();
  }

}
