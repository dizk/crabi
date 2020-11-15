package no.pew.crabi.arbitrage;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import no.pew.crabi.exchanges.ExchangeConnectionManager;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArbitrageService {

  private static final Logger logger = LoggerFactory.getLogger(ArbitrageService.class);

  private final ExchangeConnectionManager exchangeConnectionManager;

  public ArbitrageService(ExchangeConnectionManager exchangeConnectionManager) {
    this.exchangeConnectionManager = exchangeConnectionManager;
  }

  public void subscribeToAllPairs(String exchangeString) {
    StreamingExchange exchange = exchangeConnectionManager.connect(exchangeString);

    Map<CurrencyPair, CurrencyPairMetaData> currencies =
        exchange.getExchangeMetaData().getCurrencyPairs();

    Arbitrage arbitrage = new Arbitrage();

    for (CurrencyPair currencyPair : currencies.keySet()) {
      CurrencyPairMetaData meta = currencies.get(currencyPair);
      logger.info("subscribing to {}, meta: {}", currencyPair, meta);
      subscribeToPair(
          currencyPair, arbitrage, meta, exchangeString, exchange.getStreamingMarketDataService());
    }
  }

  public void subscribeToPair(
      CurrencyPair currencyPair,
      Arbitrage arbitrage,
      CurrencyPairMetaData meta,
      String exchangeString,
      StreamingMarketDataService streamingMarketDataService) {
    streamingMarketDataService
        .getOrderBook(currencyPair)
        .toFlowable(BackpressureStrategy.BUFFER)
        .parallel()
        .runOn(Schedulers.computation())
        .flatMap(
            orderBook -> {
              Optional<LimitOrder> ask = orderBook.getAsks().stream().findFirst();
              Optional<LimitOrder> bid = orderBook.getBids().stream().findFirst();

              return Flowable.fromIterable(
                  Stream.of(ask, bid)
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .collect(Collectors.toList()));
            })
        .sequential()
        .observeOn(Schedulers.single())
        .map(order -> arbitrage.addOrder(order, meta.getTradingFee()))
        .parallel()
        .runOn(Schedulers.computation())
        .flatMap(graph -> Flowable.fromIterable(Arbitrage.findPossibilities(graph)))
        .sequential()
        .subscribe(
            arbitragePossibility ->
                logger.info(
                    "\n{}\n exchange: {} meta: {}", arbitragePossibility, exchangeString, meta));
  }
}
