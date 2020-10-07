package fixme.router.models;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.io.*;
import java.nio.charset.*;
import fixme.router.controllers.RouterController;

public class ServerConnection implements CompletionHandler<AsynchronousSocketChannel, Headers> {
  private static int clientId = 100000;
  
  @Override
  public void completed(AsynchronousSocketChannel client, Headers header) {
    try {
      SocketAddress clientAddr = client.getRemoteAddress();
      System.out.format("Connected to %s%n", clientAddr);
      header.server.accept(header, this);
      Writer rwHandler = new Writer();
      Headers newHeader = new Headers();
      newHeader.server = header.server;
      newHeader.client = client;
      newHeader.clientId = clientId++;
      newHeader.buffer = ByteBuffer.allocate(2048);
      newHeader.isRead = false;
      newHeader.clientAddr = clientAddr;
      Charset cs = StandardCharsets.UTF_8;
      byte data [] = Integer.toString(newHeader.clientId).getBytes(cs);
      newHeader.rwHandler = rwHandler;
      newHeader.buffer.put(data);
      newHeader.buffer.flip();
      RouterController.addClient(newHeader);
      client.write(newHeader.buffer, newHeader, rwHandler);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void failed(Throwable e, Headers header) {
    System.out.println("Unable to connect.");
    e.printStackTrace();
  }
}
