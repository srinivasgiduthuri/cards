package in.amigoscorp.cards.controller;

import in.amigoscorp.cards.entity.Card;
import in.amigoscorp.cards.service.CardService;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController {

  private final Logger log = LoggerFactory.getLogger(CardController.class);
  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  /**
   * Get all the cards by user identifier
   *
   * @param userId a unique user identifier
   * @return the list of cards associated to the user
   */
  @GetMapping(path = {"/cards/{userId}/", "/cards/{userId}"})
  public ResponseEntity<List<Card>> getAllCardsByUserId(@PathVariable Long userId) {
    log.info("Calling GET getAllCardsByUserId() for the user id {}", userId);
    List<Card> cards = this.cardService.getAllCardsByUserId(userId);
    if (CollectionUtils.isEmpty(cards)) {
      log.warn("No cards found for the user id {}", userId);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(cards);
  }

  /**
   * Save the card information securely
   *
   * @param card {@link Card}
   * @return None
   */
  @PostMapping(path = {"/card", "/card/"})
  public ResponseEntity<Void> saveCard(@RequestBody Card card) {
    log.info("Calling GET saveCard() for the user id {}", card.getUserId());
    boolean isCardAdded = this.cardService.addCard(card);
    if (isCardAdded) {
      log.info("Card is successfully stored.");
      return ResponseEntity.accepted().build();
    } else {
      log.info("Unable to store the card details.");
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Deletes the card information from the data store as well as cache
   *
   * @param card {@link Card}
   * @return None
   */
  @DeleteMapping(path = {"/card", "/card/"})
  public ResponseEntity<Void> deleteCard(@RequestBody Card card) {
    log.info("Calling GET deleteCard() for the user id {}", card.getUserId());
    boolean isCardDeleted = this.cardService.deleteCard(card);
    if (isCardDeleted) {
      log.info("Card is successfully deleted.");
      return ResponseEntity.accepted().build();
    } else {
      log.info("Unable to delete the card details.");
      return ResponseEntity.badRequest().build();
    }
  }
}
