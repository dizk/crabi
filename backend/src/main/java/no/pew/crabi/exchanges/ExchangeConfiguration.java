package no.pew.crabi.exchanges;

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.poloniex2.PoloniexStreamingExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfiguration {

  @Bean
  public ExchangeProvider exchangeCollection() {
    ExchangeProvider collection = new ExchangeProvider();

    collection.addExchange(
        StreamingExchangeFactory.INSTANCE.createExchange(
            PoloniexStreamingExchange.class.getName()));
    collection.addExchange(
        StreamingExchangeFactory.INSTANCE.createExchange(
            BitstampStreamingExchange.class.getName()));
    collection.addExchange(
        StreamingExchangeFactory.INSTANCE.createExchange(
            BitfinexStreamingExchange.class.getName()));

    return collection;
  }
}
