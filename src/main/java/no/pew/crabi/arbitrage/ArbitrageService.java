package no.pew.crabi.arbitrage;


import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.poloniex2.PoloniexStreamingExchange;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArbitrageService {

    private static final Logger logger = LoggerFactory.getLogger(ArbitrageService.class);

    private static List<StreamingExchange> exchanges = new ArrayList<StreamingExchange>() {{
        add(StreamingExchangeFactory.INSTANCE.createExchange(PoloniexStreamingExchange.class.getName()));
        add(StreamingExchangeFactory.INSTANCE.createExchange(BitstampStreamingExchange.class.getName()));
        add(StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class.getName()));
    }};

    public ArbitrageService() {
    }

    public List<ExchangeSpecification> listExchanges() {
        return exchanges.stream().map(Exchange::getExchangeSpecification).collect(Collectors.toList());
    }


    public void connectToExchange(String exchangeClassName) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeClassName);
        exchange.connect().blockingAwait();
        logger.info("Connected to {}", exchange.getExchangeSpecification().getExchangeName());

        Map<CurrencyPair, CurrencyPairMetaData> currencies = exchange.getExchangeMetaData().getCurrencyPairs();

        Arbitrage arbitrage = new Arbitrage("0.002");

        for (CurrencyPair currencyPair : currencies.keySet()) {
            CurrencyPairMetaData meta = currencies.get(currencyPair);
            logger.info("subscribing to {}, meta: {}", currencyPair, meta);
            subscribeToPair(currencyPair, arbitrage, exchange.getStreamingMarketDataService());
        }
    }

    public void subscribeToPair(
            CurrencyPair currencyPair,
            Arbitrage arbitrage,
            StreamingMarketDataService streamingMarketDataService) {
        streamingMarketDataService
                .getOrderBook(currencyPair)
                .toFlowable(BackpressureStrategy.BUFFER)
                .parallel()
                .runOn(Schedulers.computation())
                .flatMap(orderBook -> {
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
                .map(arbitrage::addOrder)
                .parallel()
                .runOn(Schedulers.computation())
                .flatMap(graph -> Flowable.fromIterable(Arbitrage.findPossibilities(graph)))
                .sequential()
                .forEach(arbitragePossibility -> logger.info("\n{}", arbitragePossibility));
    }
}
