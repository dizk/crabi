package no.pew.crabi.exchanges;

import info.bitrich.xchangestream.core.StreamingExchange;

import java.util.ArrayList;

public class ExchangeProvider {

  private final ArrayList<StreamingExchange> exchanges = new ArrayList<>();

  public void addExchange(StreamingExchange streamingExchange) {
    this.exchanges.add(streamingExchange);
  }

  public ArrayList<StreamingExchange> getExchanges() {
    return exchanges;
  }
}
