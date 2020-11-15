package no.pew.cwclient.netty;

import ch.cryptowat.protobuf.Stream;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import no.pew.cwclient.MessageConsumer;

import java.net.URI;

import static io.netty.buffer.ByteBufUtil.hexDump;

public class WebSocketClientInitializer extends ChannelInitializer<SocketChannel> {

  private final URI uri;
  private final MessageConsumer messageConsumer;
  private final boolean ssl;

  public WebSocketClientInitializer(URI uri, MessageConsumer messageConsumer) {
    this.ssl = isSsl(uri.getScheme());
    this.uri = uri;
    this.messageConsumer = messageConsumer;
  }

  private boolean isSsl(String protocol) {
    switch (protocol) {
      case "wss":
        return true;
      case "ws":
        return false;
      default:
        throw new IllegalArgumentException("Unsupported protocol: " + protocol);
    }
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    // SSL
    if (ssl) {
      SslContext sslContext = SslContextBuilder.forClient().build();
      pipeline.addLast("ssl", sslContext.newHandler(ch.alloc(), uri.getHost(), uri.getPort()));
    }

    // HTTP
    pipeline.addLast("http-codec", new HttpClientCodec());
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));

    // WS
    // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
    // If you change it to V00, ping is not supported and remember to change
    // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
    WebSocketClientProtocolHandler webSocketClientProtocolHandler =
        new WebSocketClientProtocolHandler(
            WebSocketClientProtocolConfig.newBuilder().webSocketUri(uri).build());
    pipeline.addLast("ws-handshake", webSocketClientProtocolHandler);
    pipeline.addLast("ws-aggregate", new WebSocketFrameAggregator(65536));

    // Protobuf decode
    pipeline.addLast(new BinaryWebSocketFrameMessageToMessageDecoder());
    pipeline.addLast(new ProtobufDecoder(Stream.StreamMessage.getDefaultInstance()));

    // Protobuf encode
    pipeline.addLast(new ProtobufWebSocketEncoder());

    // Message handler
    pipeline.addLast("ws-handler", new CwWebSocketClientHandler(messageConsumer));
  }
}
