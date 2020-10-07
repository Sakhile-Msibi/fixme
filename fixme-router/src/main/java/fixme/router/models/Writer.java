package fixme.router.models;

import java.io.*;
import java.nio.channels.*;
import java.nio.charset.*;
import fixme.router.controllers.RouterController;

public class Writer implements CompletionHandler<Integer, Headers> {
  private String SOH;
  
  public Writer() {
      SOH = "" + (char)1;
  }

  @Override
  public void completed(Integer res, Headers header) {
    if (res == -1) {
      try {
        header.client.close();
        RouterController.removeClient(header.clientId);
        String port = header.server.getLocalAddress().toString().split(":")[1];
        System.out.format("[" + getServerName(port) + "] has stopped listening to the client %s%n", header.clientAddr);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      return;
    }
  
    if (header.isRead) {
      header.buffer.flip();
      int limits = header.buffer.limit();
      byte bytes[] = new byte[limits];
      header.buffer.get(bytes, 0, limits);
      Charset cs = StandardCharsets.UTF_8;
      String msg = new String(bytes, cs);
      String datum[] = msg.split(SOH);
      header.msg = datum;
      try {
        String port = header.server.getLocalAddress().toString().split(":")[1];
        System.out.format("["+ getServerName(port) +"]Client at  %s  says: %s%n", header.clientAddr, msg.replace((char)1, '|'));
      } catch(Exception e) {
        System.out.println(e);
      }
      header.isRead = false;
      header.buffer.rewind();
      header.buffer.clear();
      byte[] data = msg.getBytes(cs);
      header.buffer.put(data);
      header.buffer.flip();

      if (header.client.isOpen() && RouterController.getSize() > 1) {
        new CheckSum().performAction(header, CommInterface.CHECKSUM);
      }
    } else {
      header.isRead = true;
      header.buffer.clear();
      header.client.read(header.buffer, header, this);
    }
  }

  @Override
  public void failed(Throwable e, Headers header) {
    e.printStackTrace();
  }

  private String getServerName(String port) {
    if (port.equals("5000")) {
      return "Broker Server";
    } else {
      return "Market Server";
    }
  }
}