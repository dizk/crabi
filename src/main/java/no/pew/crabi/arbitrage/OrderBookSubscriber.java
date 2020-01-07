package no.pew.crabi.arbitrage;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;


@Service
public class OrderBookSubscriber implements Subscriber<OrderBook> {
    private static final Logger logger = LoggerFactory.getLogger(OrderBookSubscriber.class);

    private Instant timer;
    private Subscription subscription;
    private final Arbitrage arbitrage;

    public OrderBookSubscriber(Arbitrage arbitrage) {
        this.arbitrage = arbitrage;
        this.timer = Instant.now();
    }


    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(OrderBook orderBook) {
        if (!orderBook.getAsks().isEmpty()) {
            LimitOrder order = orderBook.getAsks().get(0);
            handleOrder(order);
        }

        if (!orderBook.getBids().isEmpty()) {
            LimitOrder order = orderBook.getBids().get(0);
            handleOrder(order);
        }
        this.subscription.request(1);
    }

    private void handleOrder(LimitOrder order) {
        // BTC/USD
        //
        // BTC = BASE
        // USD = COUNTER
        //
        // 8090 ASK = SELL == BTC -> USD
        //
        // 8080 BID = BUY == USD -> BTC

        arbitrage.addOrder(order);
        if (Duration.between(timer, Instant.now()).toMillis() > 10000) {
            timer = Instant.now();
            System.out.println(arbitrage.findPossibilities().get(0));
        }
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("onError()", throwable);
        this.subscription.request(1);
    }

    @Override
    public void onComplete() {
        logger.info("onComplete()");

    }
}
