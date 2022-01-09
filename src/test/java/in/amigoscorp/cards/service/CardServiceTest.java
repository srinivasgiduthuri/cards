package in.amigoscorp.cards.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import in.amigoscorp.cards.entity.Card;
import in.amigoscorp.cards.entity.CardIssuer;
import in.amigoscorp.cards.repository.CardRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CardServiceTest {

  @Autowired
  private CardService cardService;
  @MockBean
  private CardRepository cardRepository;

  @Test
  void testGetAllCardsByUserId() {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(Collections.singletonList(card)).when(cardRepository).findAllByUserId(1L);
    doReturn(card).when(cardRepository).save(card);
    cardService.addCard(card);
    List<Card> cards = cardService.getAllCardsByUserId(1L);
    Assertions.assertEquals(1, cards.size());
    Assertions.assertSame(card, cards.get(0));
  }

  @Test
  void testAddCard() {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(card).when(cardRepository).save(card);
    boolean isCardAdded = cardService.addCard(card);
    Assertions.assertTrue(isCardAdded);
  }

  @Test
  void testAddVisaCard() {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.VISA).expired(false).build();
    doReturn(card).when(cardRepository).save(card);
    boolean isCardAdded = cardService.addCard(card);
    Assertions.assertFalse(isCardAdded);
  }

  @Test
  void testDeleteCard() {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doNothing().when(cardRepository).delete(card);
    boolean isCardDeleted = cardService.deleteCard(card);
    Assertions.assertTrue(isCardDeleted);
  }

}
