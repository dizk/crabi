package no.pew.crabi.arbitrage;


import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
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

    private final StreamingExchange exchange;
    private final Arbitrage arbitrage;

    public ArbitrageService(Arbitrage arbitrage) {
        this.arbitrage = arbitrage;


        exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class.getName());
        exchange.connect().blockingAwait();
        logger.info("Connected to Bitfinex");

        Map<CurrencyPair, CurrencyPairMetaData> currencies = exchange.getExchangeMetaData().getCurrencyPairs();

        for (CurrencyPair currencyPair : currencies.keySet()) {
            logger.info("subscribing to {}", currencyPair);
            subscribeToPair(currencyPair);
        }
    }

    public void subscribeToPair(CurrencyPair currencyPair) {
        exchange.getStreamingMarketDataService()
                .getOrderBook(currencyPair)
                .toFlowable(BackpressureStrategy.BUFFER)
                .parallel()
                .runOn(Schedulers.computation())
                .flatMap(orderBook -> {
                    Optional<LimitOrder> ask = orderBook.getAsks().stream().findFirst();
                    Optional<LimitOrder> bid = orderBook.getBids().stream().findFirst();

                    return Flowable.fromIterable(
                            Stream.of(ask, bid)
                                    .flatMap(Optional::stream)
                                    .collect(Collectors.toList()));
                })
                .sequential()
                .observeOn(Schedulers.single())
                .map(arbitrage::addOrder)
                .parallel()
                .runOn(Schedulers.computation())
                .flatMap(graph -> Flowable.fromIterable(Arbitrage.findPossibilities(graph)))
                .sequential()
                .forEach(arbitragePossibility -> {
                    System.out.println(arbitragePossibility);
                });
    }
}
