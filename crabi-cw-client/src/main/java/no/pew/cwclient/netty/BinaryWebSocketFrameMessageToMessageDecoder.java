package no.pew.cwclient.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

class BinaryWebSocketFrameMessageToMessageDecoder
    extends MessageToMessageDecoder<BinaryWebSocketFrame> {
  @Override
  protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) {
    if (msg.content().capacity() == 1) {
      // Heartbeat
      return;
    }

    out.add(msg.retain().content());
  }
}
