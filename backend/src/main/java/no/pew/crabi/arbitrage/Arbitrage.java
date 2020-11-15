package no.pew.crabi.arbitrage;

import no.pew.crabi.graph.ImmutableGraph;
import no.pew.crabi.graph.MutableGraphImpl;
import no.pew.crabi.graph.NegativeCycle;
import org.apache.commons.lang3.tuple.Pair;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Arbitrage {

    private final MutableGraphImpl<String, LimitOrder> cycleGraph;

    public Arbitrage() {
        this.cycleGraph = new MutableGraphImpl<>();
    }

    public ImmutableGraph<String, LimitOrder> addOrder(LimitOrder order, BigDecimal tradingFee) {
        CurrencyPair currencyPair = order.getCurrencyPair();

        BigDecimal bidFee = BigDecimal.ONE.subtract(tradingFee);
        BigDecimal askFee = BigDecimal.ONE.add(tradingFee);

        if (order.getType().equals(Order.OrderType.BID)) {
            // BID = counter -> base (quoted price)
            cycleGraph.upsertEdge(
                    currencyPair.counter.getCurrencyCode(),
                    currencyPair.base.getCurrencyCode(),
                    -Math.log(order.getLimitPrice().multiply(bidFee).doubleValue()),
                    order);
        } else {
            // ASK = counter -> base (inverse quoted price)
            // ln(1/x) = ln(x^⁻1) = -ln(x)
            // This means in our case we can use log of the price without inverting it :)
            cycleGraph.upsertEdge(
                    currencyPair.base.getCurrencyCode(),
                    currencyPair.counter.getCurrencyCode(),
                    Math.log(order.getLimitPrice().multiply(askFee).doubleValue()),
                    order);
        }

        return cycleGraph.snapshot();
    }

    public static List<ArbitragePossibility> findPossibilities(ImmutableGraph<String, LimitOrder> graph) {
        List<List<Pair<String, LimitOrder>>> cycles = NegativeCycle.findCycle(graph);

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
