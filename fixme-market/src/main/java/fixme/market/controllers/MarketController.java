package fixme.market.controllers;

import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.*;
import fixme.market.models.Headers;
import fixme.market.models.Writer;

public class MarketController {
    private static int quantity;
    private static int price;
    private static int req;
    private static int dstnId;
    private static final String fixversion = "8=FIX.4.2";
    private static Headers header;
    
    public MarketController(int quantity1, int price1) {

    }

    public void contact() throws Exception {
        AsynchronousSocketChannel servers = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = servers.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        header = new Headers();
        header.client = servers;
        header.buffer = ByteBuffer.allocate(2048);
        header.isRead = true;
        header.mainThread = Thread.currentThread();
        Writer writer = new Writer();
        servers.read(header.buffer, header, writer);
        try {
            Thread.currentThread().join();
        } catch(Exception e) {
            
        }
    }
    
    public static String processRequest(String res) {
        String data[] = res.split("" + (char)1);
        String msgType="";
        String reqType="";
        String price="";
        String quantity1="";

        for(String dataa : data) {
            if (dataa.contains("35=")) {
                msgType = dataa.split("=")[1];
            }
            else if (dataa.contains("54=")) {
                reqType = dataa.split("=")[1];
            }
            else if (dataa.contains("44=")) {
                price = dataa.split("=")[1];
            }
            else if (dataa.contains("38=")) {
                quantity1 = dataa.split("=")[1];
            }
            else if (dataa.contains("id=")) {
                dstnId = Integer.parseInt(dataa.split("=")[1]);
            }
        }
        return process(msgType, reqType, price, quantity1);
    }

    private static String process(String msgType, String reqType, String price1, String quantity1) {
        int price2 = Integer.parseInt(price1);
        int quantity2 = Integer.parseInt(quantity1);

        if (msgType.equals("D") && reqType.equals("2") && price2 < price && (req == 2 || req == 3)) {
            return getMessage(3, Integer.parseInt(quantity1)); //buy from broker
        } else if (msgType.equals("D") && reqType.equals("1") && price2 >= price && quantity - quantity2 >= 0 && (req == 2 || req == 3)) {
            return getMessage(2, Integer.parseInt(quantity1)); //sell to broker
        } else {
            return getMessage(1, Integer.parseInt(quantity1)); //reject broker request
        }
    }

    private static String getMessage(int option, int quantity1) {
        String soh = "" + (char)1;
        String msg = "";

        if (option == 1) {
            msg = "id="+header.clientId+soh+fixversion+soh+"35=8"+soh+"39=8"+soh+"50="+header.clientId+soh+"49="+header.clientId+soh+"56="+dstnId+soh;
        }
        if (option == 2) {
            msg = "id="+header.clientId+soh+fixversion+soh+"35=8"+soh+"39=2"+soh+"50="+header.clientId+soh+"49="+header.clientId+soh+"56="+dstnId+soh;
            quantity -= quantity1;
        }
        if (option == 3) {
            msg = "id="+header.clientId+soh+fixversion+soh+"35=8"+soh+"39=2"+soh+"50="+header.clientId+soh+"49="+header.clientId+soh+"56="+dstnId+soh;
            quantity += quantity1;
        }
        return msg + getCheckSum(msg);
    }

    private static String getCheckSum(String msg) {
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
}