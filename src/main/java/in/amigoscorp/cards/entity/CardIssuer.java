package in.amigoscorp.cards.entity;

public enum CardIssuer {
  MASTERCARD("MasterCard"), VISA("VISA");
  public final String label;

  private CardIssuer(String label) {
    this.label = label;
  }
}
