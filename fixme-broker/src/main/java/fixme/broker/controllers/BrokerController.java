package fixme.broker.controllers;

import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.concurrent.*;
import fixme.broker.models.Headers;
import fixme.broker.models.Writer;

public class BrokerController {
    private static int quantity = 10;
    private static int cash = 10000000;
    private static Headers header;
    private static final String fixversion = "8=FIX.4.2";
    public static int brokerS;
    public static int dstnId;

    public BrokerController(int dstnd1, int brokerS1) {
        dstnId = dstnd1;
        brokerS = brokerS1;
    }

    public void contact() throws Exception {
        AsynchronousSocketChannel servers = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
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
        } catch (Exception e) {
            
        }
    }

    public static String sellProduct(int dstnId) {
        String soh = "" + (char)1;
        String msg = "id="+header.clientId+soh+fixversion+soh+"35=D"+soh+"54=2"+soh+"38=2"+soh+"44=55"+soh+"55=ABG"+soh;
        msg += "50="+header.clientId+soh+"49="+header.clientId+soh+"56="+dstnId+soh;

        if (quantity > 0) {
            return msg;
        } else {
            return "bye";
        }
    }

    public static String buyProduct(int dstnId) {
        String soh = "" + (char)1;
        String msg = "id="+header.clientId+soh+fixversion+soh+"35=D"+soh+"54=1"+soh+"38=2"+soh+"44=90"+soh+"55=CPI"+soh;
        msg += "50="+header.clientId+soh+"49="+header.clientId+soh+"56="+dstnId+soh;
        
        if (cash > 0) {
            return msg;
        } else {
            return "bye";
        }
    }

    public static boolean proccessReply(String reply) {
        String data[] = reply.split(""+(char)1);
        String tag = "";
        String state = "";

        for(String dataa : data) {
            if (dataa.contains("35=")) {
                tag = dataa.split("=")[1];
            }
            if (dataa.contains("39=")) {
                state = dataa.split("=")[1];
            }
        }
        if (tag.equals("8") && state.equals("8")) {
            System.out.println("\nMarket[" + dstnId +"] rejected order\n");
            return false;
        }
        if (tag.equals("8") && state.equals("2")) {
            System.out.println("\nMarket[" + dstnId +"] accepted order\n");
            return true;
        }
        return false;
    }

    public static void updateData(boolean state) {
        if (state == false) {
            quantity -= 2;
            cash += 55;
        } else {
            quantity += 2;
            cash -= 90;
        }   
    }
}