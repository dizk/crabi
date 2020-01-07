package no.pew.crabi.arbitrage;


import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Scheduler;
import io.reactivex.internal.schedulers.ComputationScheduler;
import io.reactivex.schedulers.Schedulers;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ArbitrageService {

    private static final Logger logger = LoggerFactory.getLogger(ArbitrageService.class);

    private final StreamingExchange exchange;
    private final OrderBookSubscriber subscriber;

    public ArbitrageService(OrderBookSubscriber subscriber) {
        this.subscriber = subscriber;


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
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }


}
