package fixme.market.controllers;

public class Market {
  public static void main(String[] args) {
    MarketController market = new MarketController(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    
    try {
      market.contact();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}