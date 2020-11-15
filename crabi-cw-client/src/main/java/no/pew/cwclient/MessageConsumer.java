package no.pew.cwclient;

import ch.cryptowat.protobuf.Stream;

public interface MessageConsumer {
  void accept(Stream.StreamMessage msg);
}
