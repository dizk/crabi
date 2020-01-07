package no.pew.crabi.arbitrage;

import no.pew.crabi.graph.ImmutableGraph;
import no.pew.crabi.graph.MutableGraphImpl;
import no.pew.crabi.graph.NegativeCycle;
import org.apache.commons.lang3.tuple.Pair;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Arbitrage {

    private static final Logger logger = LoggerFactory.getLogger(Arbitrage.class);

    private final MutableGraphImpl<String, LimitOrder> cycleGraph;

    public Arbitrage() {
        this.cycleGraph = new MutableGraphImpl<>();
    }

    public void addOrder(LimitOrder order) {
        CurrencyPair currencyPair = order.getCurrencyPair();

        if (order.getType().equals(Order.OrderType.BID)) {
            // BID = counter -> base (quoted price)
            cycleGraph.upsertEdge(
                    currencyPair.counter.getCurrencyCode(),
                    currencyPair.base.getCurrencyCode(),
                    -Math.log(order.getLimitPrice().doubleValue()),
                    order);
        } else {
            // ASK = counter -> base (inverse quoted price)
            // ln(1/x) = ln(x⁻1) = -ln(x)
            // This mean in our case we can use log of the price without inverting it :)
            cycleGraph.upsertEdge(
                    currencyPair.base.getCurrencyCode(),
                    currencyPair.counter.getCurrencyCode(),
                    Math.log(order.getLimitPrice().doubleValue()),
                    order);
        }

    }

    public List<ArbitragePossibility> findPossibilities() {
        ImmutableGraph<String, LimitOrder> snapshot = cycleGraph.snapshot();
        List<List<Pair<String, LimitOrder>>> cycles = NegativeCycle.findCycle(snapshot);

        return cycles
                .stream()
                .map(l ->
                        new ArbitragePossibility(
                                l.stream()
                                        .map(Pair::getKey)
                                        .collect(Collectors.toList()),
                                l
                                        // Remove the last element of the cycle as it will be the same as the first
                                        .subList(0, l.size() - 1)
                                        .stream()
                                        .map(p -> new ArbitrageStep(p.getValue()))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "Arbitrage{" +
                "cycleGraph=" + cycleGraph +
                '}';
    }
}
