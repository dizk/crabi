package no.pew.crabi.api;

import no.pew.crabi.arbitrage.ArbitrageService;
import no.pew.crabi.exchanges.ExchangeConnectionManager;
import no.pew.crabi.model.Exchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangesService implements ExchangesApiDelegate {

  private final ExchangeConnectionManager exchangeConnectionManager;
  private final ArbitrageService arbitrageService;

  public ExchangesService(
      ExchangeConnectionManager exchangeConnectionManager, ArbitrageService arbitrageService) {
    this.exchangeConnectionManager = exchangeConnectionManager;
    this.arbitrageService = arbitrageService;
  }

  @Override
  public ResponseEntity<List<Exchange>> getExchanges() {
    return new ResponseEntity<>(
        exchangeConnectionManager.getAvailableExchanges().stream()
            .map(exchangeName -> new Exchange().exchangeName(exchangeName))
            .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> connectToExchange(Exchange exchange) {
    arbitrageService.subscribeToAllPairs(exchange.getExchangeName());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
