package fixme.broker.controllers;

public class Broker {
  public static void main(String[] args) throws Exception {
    BrokerController broker = new BrokerController(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    
    try {
      broker.contact();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}