package no.pew.crabi.exchanges;

import info.bitrich.xchangestream.core.StreamingExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangeConnectionManager {

  private final Map<String, StreamingExchange> exchanges;
  private static final Logger logger = LoggerFactory.getLogger(ExchangeConnectionManager.class);

  public ExchangeConnectionManager(ExchangeProvider provider) {
    this.exchanges =
        provider.getExchanges().stream()
            .collect(Collectors.toMap(e -> e.getExchangeSpecification().getExchangeName(), e -> e));
  }

  public StreamingExchange connect(String exchangeName) {
    StreamingExchange exchange = exchanges.get(exchangeName);
    if (!exchange.isAlive()) {
      exchange.connect().blockingAwait();
      logger.info("Connected to {}", exchange.getExchangeSpecification().getExchangeName());
    }

    return exchange;
  }

  public Collection<String> getAvailableExchanges() {
    return exchanges.keySet();
  }
}
