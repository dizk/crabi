package no.pew.crabi.orderbook.events;

import java.math.BigDecimal;

public class OrderEntryEvent {
    private final String id;
    private final Side side;
    private final BigDecimal price;
    private final BigDecimal volume;

    public OrderEntryEvent(String id, Side side, BigDecimal price, BigDecimal volume) {
        this.id = id;
        this.side = side;
        this.price = price;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getVolume() {
        return volume;
    }
}
