package fixme.router.controllers;

public class Router {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 5000;
    RouterController route = new RouterController(host, port);
    try {
      route.startServers();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}