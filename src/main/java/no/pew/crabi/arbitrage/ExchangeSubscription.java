package no.pew.crabi.arbitrage;

public class ExchangeSubscription {

    private final boolean ok;

    public ExchangeSubscription(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }
}
