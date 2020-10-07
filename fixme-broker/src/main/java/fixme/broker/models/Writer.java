package fixme.broker.models;

import java.nio.charset.*;
import java.nio.channels.*;
import fixme.broker.controllers.BrokerController;

public class Writer implements CompletionHandler<Integer, Headers> {
    @Override
    public void completed(Integer res, Headers header) {
      if(res == -1) {
        header.mainThread.interrupt();
        System.out.println("Broker server is going offline...");
        return ;
      }

      if (header.isRead) {
        header.buffer.flip();
        Charset cs = StandardCharsets.UTF_8;
        int limits = header.buffer.limit();
        byte bytes[] = new byte[limits];
        header.buffer.get(bytes, 0, limits);
        String msg = new String(bytes, cs);
        if(header.clientId == 0) {
          header.clientId = Integer.parseInt(msg);
          System.out.println("Server responded with Id: " + header.clientId);
        } else {
          System.out.println("Server Responded: "+ msg.replace((char)1, '|'));
        }

        try {
          boolean checker = BrokerController.proccessReply(msg);
          if (checker == true && BrokerController.brokerS == 1) {
            BrokerController.updateData(true);
          }
          if (checker == true && BrokerController.brokerS == 0) {
            BrokerController.updateData(false);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        header.buffer.clear();
        msg = testMe(header);
        if (msg.contains("bye") || i > 3) {
          header.mainThread.interrupt();
          return;
        }
        i++;
        System.out.println("\nBroker response:" + msg.replace((char)1, '|'));
        byte[] data = msg.getBytes(cs);
        header.buffer.put(data);
        header.buffer.flip();
        header.isRead = false;
        header.client.write(header.buffer, header, this);
      }else {
        header.isRead = true;
        header.buffer.clear();
        header.client.read(header.buffer, header, this);
      }
    }

    @Override
    public void failed(Throwable e, Headers header) {
      e.printStackTrace();
    }

    private String testMe(Headers header){
      String msg;

      if(BrokerController.brokerS == 1) {
        msg = BrokerController.buyProduct(BrokerController.dstnId);
      } else {
        msg = BrokerController.sellProduct(BrokerController.dstnId);
      }
      return msg + getCheckSum(msg);
    }

    private String getCheckSum(String msg) {
        int j = 0;
        char t[];
        String soh = "" + (char)1;
        String datum[] = msg.split(soh);
            
        for(int k = 0; k < datum.length; k++) {
            t = datum[k].toCharArray();
            for(int i = 0; i < t.length; i++) {
                j += (int)t[i];
            }
            j += 1;
        }
        return ("10="+ (j % 256) + soh);
    }

    private static int i = 0;
  }