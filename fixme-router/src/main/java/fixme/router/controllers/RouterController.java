package fixme.router.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import fixme.router.models.Headers;
import fixme.router.models.StartServer;

public class RouterController {
    private String host1;
    private int port1;
    private static List<Headers> clients = new ArrayList<>();

    public RouterController(String host, int port) {
        host1 = host;
        port1 = port;
    }

    public static void addClient(Headers client) {
        clients.add(client);
    }

    public static Headers getClient(int id) {
        for(Headers client : clients) {
            if (client.clientId == id) {
                return client;
            }
        }
        return null;
    }

    public static int getSize() {
        return clients.size();
    }

    public void startServers() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(new StartServer(host1, port1));
        pool.submit(new StartServer(host1, port1 + 1));
        pool.shutdown();
    }

    public static void removeClient(int id) {
        try {
            clients.remove(getClient(id));
        } catch(Exception e) {
            //TODO: handle exception
        }
    }
}