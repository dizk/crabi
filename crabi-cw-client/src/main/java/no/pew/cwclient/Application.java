package no.pew.cwclient;

import java.io.IOException;

public class Application {

  public static void main(String[] args) throws Exception {
    final CwWebSocketClient client =
        new CwWebSocketClient("AW5HGM5MOU6E0PCW00YK", System.out::println);

    new Thread(
            () -> {
              try {
                Thread.sleep(5000);
                client.subscribe("instruments:232:book:deltas");
                Thread.sleep(5000);
                client.unsubscribe("instruments:232:book:deltas");
                client.close();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            })
        .start();
    client.open();
  }
}
