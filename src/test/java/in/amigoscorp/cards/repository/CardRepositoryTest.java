package in.amigoscorp.cards.repository;

import in.amigoscorp.cards.entity.Card;
import in.amigoscorp.cards.entity.CardIssuer;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CardRepositoryTest {

  @Autowired
  private CardRepository cardRepository;

  @Test
  void testFindAllByUserId() {
    Card card = Card.builder().userId(1L).cardNumber("1").cardIssuer(CardIssuer.MASTERCARD)
        .expired(false).build();
    cardRepository.save(card);
    List<Card> cards = cardRepository.findAllByUserId(1L);
    Assertions.assertEquals(1, cards.size());
    Assertions.assertEquals(card.getUserId(), cards.get(0).getUserId());
  }

  @Test
  void testFindAllByUserIdAndExpiredFalse() {
    Card card1 = Card.builder().userId(1L).cardNumber("1").cardIssuer(CardIssuer.MASTERCARD)
        .expired(false).build();
    cardRepository.save(card1);
    Card card2 = Card.builder().userId(1L).cardNumber("2").cardIssuer(CardIssuer.VISA)
        .expired(true).build();
    cardRepository.save(card2);
    List<Card> cards = cardRepository.findAllByUserIdAndExpired(1L, false);
    Assertions.assertEquals(1, cards.size());
    Assertions.assertEquals(card1.getUserId(), cards.get(0).getUserId());
    Assertions.assertEquals(card1.getCardNumber(), cards.get(0).getCardNumber());
  }

  @Test
  void testFindAllByUserIdAndExpiredTrue() {
    Card card1 = Card.builder().userId(1L).cardNumber("1").cardIssuer(CardIssuer.MASTERCARD)
        .expired(false).build();
    cardRepository.save(card1);
    Card card2 = Card.builder().userId(1L).cardNumber("2").cardIssuer(CardIssuer.VISA)
        .expired(true).build();
    cardRepository.save(card2);
    List<Card> cards = cardRepository.findAllByUserIdAndExpired(1L, true);
    Assertions.assertEquals(1, cards.size());
    Assertions.assertEquals(card2.getUserId(), cards.get(0).getUserId());
    Assertions.assertEquals(card2.getCardNumber(), cards.get(0).getCardNumber());
  }
}
