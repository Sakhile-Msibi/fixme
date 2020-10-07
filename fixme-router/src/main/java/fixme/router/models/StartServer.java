package fixme.router.models;

import java.nio.channels.*;
import java.net.*;

public class StartServer implements Runnable {
    private String host1;
    private int port1;
    
	public StartServer(String host, int port) {
        host1 = host;
        port1 = port;
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
            InetSocketAddress serverAddr = new InetSocketAddress(host1, port1);
            server.bind(serverAddr);
            if (port1 % 2 == 0) {
                System.out.format("Broker server is listening at %s%n", serverAddr);
            } else {
                System.out.format("Market server is listening at %s%n", serverAddr);
            }
            Headers header = new Headers();
            header.server = server;
            server.accept(header, new ServerConnection());
            Thread.currentThread().join();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}