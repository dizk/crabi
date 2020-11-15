package no.pew.crabi.exchanges;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Completable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExchangeConnectionManagerTest {

  private final StreamingExchange exchangeMock = mock(StreamingExchange.class);
  private final ExchangeSpecification exchangeSpecification = mock(ExchangeSpecification.class);

  private ExchangeProvider setupProvider() {
    ExchangeProvider provider = new ExchangeProvider();
    provider.addExchange(exchangeMock);

    when(exchangeSpecification.getExchangeName()).thenReturn("FakeExchange");
    when(exchangeMock.getExchangeSpecification()).thenReturn(exchangeSpecification);
    when(exchangeMock.connect()).thenReturn(Completable.fromCallable(() -> null));
    return provider;
  }

  @Test
  void connectToExchange() {
    ExchangeProvider provider = setupProvider();

    ExchangeConnectionManager connectionManager = new ExchangeConnectionManager(provider);

    connectionManager.connect("FakeExchange");

    verify(exchangeMock, times(1)).connect();
  }

  @Test
  void connectIsOnlyCalledOnce() {
    ExchangeProvider provider = setupProvider();

    ExchangeConnectionManager connectionManager = new ExchangeConnectionManager(provider);

    connectionManager.connect("FakeExchange");

    when(exchangeMock.isAlive()).thenReturn(true);

    connectionManager.connect("FakeExchange");
    connectionManager.connect("FakeExchange");
    connectionManager.connect("FakeExchange");
    connectionManager.connect("FakeExchange");
    connectionManager.connect("FakeExchange");

    verify(exchangeMock, times(1)).connect();
  }

  @Test
  void getAvailableExchanges() {
    ExchangeProvider provider = setupProvider();

    ExchangeConnectionManager connectionManager = new ExchangeConnectionManager(provider);

    assertThat(connectionManager.getAvailableExchanges()).contains("FakeExchange");
  }

  @Test
  void throwsExceptionOnNonExistingExchange() {
    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          ExchangeConnectionManager connectionManager =
              new ExchangeConnectionManager(new ExchangeProvider());
          connectionManager.connect("im_not_real");
        });
  }
}
