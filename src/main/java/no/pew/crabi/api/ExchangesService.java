package no.pew.crabi.api;

import no.pew.crabi.arbitrage.ArbitrageService;
import no.pew.crabi.model.Exchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangesService implements ExchangesApiDelegate {

  private final ArbitrageService arbitrageService;

  public ExchangesService(ArbitrageService arbitrageService) {
    this.arbitrageService = arbitrageService;
  }

  @Override
  public ResponseEntity<List<Exchange>> getExchanges() {
    return new ResponseEntity<>(
        arbitrageService.listExchanges().stream()
            .map(
                exchangeSpecification ->
                    new Exchange()
                        .exchangeClass(exchangeSpecification.getExchangeClassName())
                        .exchangeName(exchangeSpecification.getExchangeName()))
            .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> connectToExchange(Exchange exchange) {
    arbitrageService.connectToExchange(exchange.getExchangeClass());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
