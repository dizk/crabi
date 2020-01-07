package no.pew.crabi.arbitrage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class ArbitragePossibility {

    private final List<String> path;
    private final List<ArbitrageStep> arbitrageSteps;

    public ArbitragePossibility(List<String> path, List<ArbitrageStep> arbitrageSteps) {
        this.path = path;
        this.arbitrageSteps = arbitrageSteps;
    }


    public BigDecimal getRate() {
        BigDecimal rate = BigDecimal.ONE;
        for (ArbitrageStep step : arbitrageSteps) {
            rate = rate.multiply(step.getRate());
        }

        return rate;
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,###.000000");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(path);
        stringBuilder.append("\n");
        for (ArbitrageStep arbitrageStep : arbitrageSteps) {
            stringBuilder.append(arbitrageStep.getFrom());
            stringBuilder.append(" -> (");
            stringBuilder.append(String.format("%12s", df.format(arbitrageStep.getRate())));
            stringBuilder.append(") -> ");
            stringBuilder.append(arbitrageStep.getTo());
            stringBuilder.append("\t");
            stringBuilder.append(arbitrageStep.getOrder().getCurrencyPair());
            stringBuilder.append(" @ ");
            stringBuilder.append(df.format(arbitrageStep.getPrice()));
            stringBuilder.append(" V: ");
            stringBuilder.append(df.format(arbitrageStep.volume()));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
