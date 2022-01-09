package in.amigoscorp.cards.repository;

import in.amigoscorp.cards.entity.Card;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository is responsible for handling all the data store operations for the card model.
 */
@Repository
public interface CardRepository extends PagingAndSortingRepository<Card, Long> {

  /**
   * Fetches all the cards information related to a particular user using user identifier.
   *
   * @param userId an uniquer user identifier
   * @return a list of cards information
   */
  List<Card> findAllByUserId(Long userId);

  /**
   * Fetches all the cards information related to a particular user using user identifier and the
   * expiry state.
   *
   * @param userId    an uniquer user identifier
   * @param isExpired a flag that represents the expiry state. {@code true} represents the card is
   *                  expired. {@code false} represents the card is not expired.
   * @return a list of cards information
   */
  List<Card> findAllByUserIdAndExpired(Long userId, boolean isExpired);
}
