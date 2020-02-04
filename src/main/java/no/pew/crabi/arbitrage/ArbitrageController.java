package no.pew.crabi.arbitrage;

import org.knowm.xchange.ExchangeSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping("api")
public class ArbitrageController {
    private final ArbitrageService arbitrageService;

    public ArbitrageController(ArbitrageService arbitrageService) {
        this.arbitrageService = arbitrageService;
    }

    @GetMapping("/exchanges")
    public List<ExchangeSpecification> exchanges() {
        return arbitrageService.listExchanges();
    }

    @PostMapping("/exchanges/{exchangeClassName}/connect")
    public ResponseEntity<ExchangeSubscription> connect(@PathVariable("exchangeClassName") String exchangeClassName) {
        arbitrageService.connectToExchange(exchangeClassName);
        return new ResponseEntity<>(new ExchangeSubscription(true), HttpStatus.OK);
    }

}
