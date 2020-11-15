package no.pew.cwclient.netty;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ProtobufWebSocketEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder>
    implements WebSocketFrameEncoder {
  @Override
  protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) {
    if (msg instanceof MessageLite) {
      out.add(new BinaryWebSocketFrame(wrappedBuffer(((MessageLite) msg).toByteArray())));
      return;
    }
    if (msg instanceof MessageLite.Builder) {
      out.add(
          new BinaryWebSocketFrame(
              wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray())));
    }
  }
}
