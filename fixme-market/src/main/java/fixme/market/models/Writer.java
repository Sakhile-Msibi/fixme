package fixme.market.models;

import java.nio.charset.*;
import java.nio.channels.*;
import fixme.market.controllers.MarketController;

public class Writer implements CompletionHandler<Integer, Headers> {
    @Override
    public void completed(Integer res, Headers header) {
      if(res == -1) {
        header.mainThread.interrupt();
        System.out.println("Market server is going offline...");
        return ;
      }
      if (header.isRead) {
        header.buffer.flip();
        Charset cs = StandardCharsets.UTF_8;
        int limits = header.buffer.limit();
        byte bytes[] = new byte[limits];
        header.buffer.get(bytes, 0, limits);
        String msg = new String(bytes, cs);
        if (header.clientId == 0) {
          header.clientId = Integer.parseInt(msg);
          System.out.println("Server Responded with Id: " + header.clientId);
          header.isRead = false;
          header.client.read(header.buffer, header, this);
          return ;
        } else {
          System.out.println("Server Responded: "+ msg.replace((char)1, '|'));
        }
        header.buffer.clear();
        msg = MarketController.processRequest(msg);
        if (msg.contains("bye")) {
          header.mainThread.interrupt();
          return;
        }

        try {
          System.out.println("\nMarket Response: "+ msg.replace((char)1, '|'));
        } catch (Exception e) {
         
        }
        byte[] data = msg.getBytes(cs);
        header.buffer.put(data);
        header.buffer.flip();
        header.isRead = false; 
        header.client.write(header.buffer, header, this);
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
  }