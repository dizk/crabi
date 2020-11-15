package no.pew.cwclient.netty;

import ch.cryptowat.protobuf.Stream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import no.pew.cwclient.MessageConsumer;

public class CwWebSocketClientHandler extends SimpleChannelInboundHandler<Stream.StreamMessage> {

  private final MessageConsumer messageConsumer;

  public CwWebSocketClientHandler(MessageConsumer messageConsumer) {
    this.messageConsumer = messageConsumer;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Stream.StreamMessage msg) {
    messageConsumer.accept(msg);
  }
}
