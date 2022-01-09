package in.amigoscorp.cards.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import in.amigoscorp.cards.entity.Card;
import in.amigoscorp.cards.entity.CardIssuer;
import in.amigoscorp.cards.repository.CardRepository;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service responsible for all the card related operations. Any business logic that is required
 * can be implemented and then pass the final card information to the data layer for persisting.
 */
@Service
public class CardService {

  private final Logger log = LoggerFactory.getLogger(CardService.class);
  private final CardRepository cardRepository;
  private final LoadingCache<Long, List<Card>> cardsCache;

  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
    cardsCache = CacheBuilder.newBuilder().build(new CacheLoader<Long, List<Card>>() {
      @Override
      public List<Card> load(Long userId) {
        log.info("Fetching the cards information for the user id {}", userId);
        return cardRepository.findAllByUserId(userId);
      }
    });
  }

  /**
   * Get all the cards by the user identifier from the cache. If they are not available in the
   * cache, fallbacks to fetch the same from the data store.
   *
   * @param userId an unique user identifier
   * @return a list of cards information
   */
  public List<Card> getAllCardsByUserId(Long userId) {
    try {
      log.info("Fetching the cards from the cache.");
      return cardsCache.get(userId);
    } catch (ExecutionException e) {
      log.error("Unable to get the cards for the user id {}", userId);
      log.info("Falling back to fetch from data store directly.");
      return cardRepository.findAllByUserId(userId);
    } catch (Exception ex) {
      log.error("Unable to get the cards for the user id {}", userId, ex);
      return Collections.emptyList();
    }
  }

  /**
   * Add the card information securely by hashing the card number and masking the card number except
   * the last 4 digits for representational purpose.
   *
   * @param card {@link Card}
   * @return the status of whether the card information is stored to data store or not. {@code true}
   * represents the card is stored. {@code false} represents the card is not stored.
   */
  public boolean addCard(Card card) {
    try {
      log.info("Adding the card information securely");
      card.setHashedCardNumber(hashCardNumber(card.getCardNumber()));
      card.setCardNumber(DigestUtils.shaHex(card.getCardNumber()));
      boolean areCardDetailsValid = verifyCardDetails(card);
      if (areCardDetailsValid) {
        cardRepository.save(card);
        cardsCache.refresh(card.getUserId());
        return true;
      } else {
        log.error("The card details provided are invalid.");
        return false;
      }
    } catch (Exception e) {
      log.error("Unable to add the card.", e);
      return false;
    }
  }

  /**
   * Remove the card information from the data store as well as from the cache.
   *
   * @param card {@link Card}
   * @return the status of whether the card information is removed from the data store or not.
   * {@code true} represents the card is removed. {@code false} represents the card is not removed.
   */
  public boolean deleteCard(Card card) {
    try {
      log.info("Deleting the card information securely");
      cardRepository.delete(card);
      cardsCache.refresh(card.getUserId());
      return true;
    } catch (Exception e) {
      log.error("Unable to delete the card.", e);
      return false;
    }
  }

  /**
   * A utility method to convert the card number to a masked card number
   *
   * @param cardNumber actual card number
   * @return the masked card number
   */
  private String hashCardNumber(String cardNumber) {
    String substring = StringUtils.substring(cardNumber, 0, cardNumber.length() - 4);
    String hash = StringUtils.repeat("X", substring.length());
    return StringUtils.replace(cardNumber, substring, hash);
  }

  /**
   * Verify the card details by calling the card issuer
   *
   * @param card {@link Card}
   * @return whether the card details are valid or not. {@code true} represents the card details are
   * valid. {@code false} represents the card details are not valid.
   */
  private boolean verifyCardDetails(Card card) {
    return card.getCardIssuer().equals(CardIssuer.MASTERCARD);
  }
}
