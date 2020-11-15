package no.pew.crabi.orderbook;

import no.pew.crabi.orderbook.events.OrderEntryEvent;
import no.pew.crabi.orderbook.events.OrderRemoveEvent;

public interface Book {
    void handleOrderEntryEvent(OrderEntryEvent orderEntryEvent);
    void handleOrderRemoveEvent(OrderRemoveEvent orderRemoveEvent);
}
