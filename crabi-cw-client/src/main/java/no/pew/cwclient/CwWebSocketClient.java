package no.pew.cwclient;

import ch.cryptowat.protobuf.Client;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.*;
import no.pew.cwclient.netty.WebSocketClientInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class CwWebSocketClient {
  private final URI uri;
  private final MessageConsumer messageConsumer;
  private static final EventLoopGroup group = new NioEventLoopGroup();
  private static final Logger logger = LoggerFactory.getLogger(CwWebSocketClient.class);
  private Channel ch;

  public CwWebSocketClient(final String apiKey, MessageConsumer messageConsumer) {
    this.uri = URI.create("wss://stream.cryptowat.ch:443/connect?format=binary&apikey=" + apiKey);
    this.messageConsumer = messageConsumer;
  }

  public void unsubscribe(String resource) {
    Client.StreamSubscription.Builder streamSubscription =
        Client.StreamSubscription.newBuilder().setResource(resource);

    Client.ClientSubscription.Builder clientSubscription =
        Client.ClientSubscription.newBuilder().setStreamSubscription(streamSubscription);

    Client.ClientUnsubscribeMessage.Builder subscribeMessage =
        Client.ClientUnsubscribeMessage.newBuilder().addSubscriptions(clientSubscription);

    Client.ClientMessage.Builder msg =
        Client.ClientMessage.newBuilder().setUnsubscribe(subscribeMessage);

    send(msg);
  }

  public void subscribe(String resource) {
    Client.StreamSubscription.Builder streamSubscription =
        Client.StreamSubscription.newBuilder().setResource(resource);

    Client.ClientSubscription.Builder clientSubscription =
        Client.ClientSubscription.newBuilder().setStreamSubscription(streamSubscription);

    Client.ClientSubscribeMessage.Builder subscribeMessage =
        Client.ClientSubscribeMessage.newBuilder().addSubscriptions(clientSubscription);

    Client.ClientMessage.Builder msg =
        Client.ClientMessage.newBuilder().setSubscribe(subscribeMessage);

    send(msg);
  }

  public void open() throws Exception {
    Bootstrap b = new Bootstrap();
    b.group(group)
        .channel(NioSocketChannel.class)
        .remoteAddress(uri.getHost(), uri.getPort())
        .handler(new WebSocketClientInitializer(uri, messageConsumer));

    logger.debug("WebSocket Client connecting...");
    ch = b.connect().sync().channel();
    logger.debug("WebSocket Client connected.");
  }

  public void close() throws InterruptedException {
    logger.debug("WebSocket Client shutting down...");
    ch.writeAndFlush(new CloseWebSocketFrame());
    ch.closeFuture().sync();
    group.shutdownGracefully();
    logger.debug("WebSocket Client shut down successfully");
  }

  private void send(final MessageLiteOrBuilder msg) {
    logger.trace("Sending {}", msg);
    ch.writeAndFlush(msg);
  }
}
