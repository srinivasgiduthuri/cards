package in.amigoscorp.cards.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.amigoscorp.cards.entity.Card;
import in.amigoscorp.cards.entity.CardIssuer;
import in.amigoscorp.cards.service.CardService;
import java.util.Collections;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

  @MockBean
  private CardService cardService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("GET /cards/1 Found")
  void testGetCardsByUserIdFound() throws Exception {
    Card mockCard = Card.builder().userId(1L).cardNumber("1").cardIssuer(CardIssuer.MASTERCARD)
        .expired(false).build();
    doReturn(Collections.singletonList(mockCard)).when(cardService).getAllCardsByUserId(1L);
    mockMvc.perform(get("/cards/{userId}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].userId", is(1)))
        .andExpect(jsonPath("$[0].cardIssuer", is("MASTERCARD")));
  }

  @Test
  @DisplayName("GET /cards/1 Not Found")
  void testGetCardsByUserIdNotFound() throws Exception {
    doReturn(Collections.emptyList()).when(cardService).getAllCardsByUserId(1L);
    mockMvc.perform(get("/cards/{userId}", 1))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("POST /card Added")
  void testSaveCardAdded() throws Exception {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    Card mockCard = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber(DigestUtils.shaHex("1234567890123456"))
        .hashedCardNumber(hashCardNumber("1234567890123456")).expiryMonth(12).expiryYear(99)
        .cvv(123).cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(true).when(cardService).addCard(card);
    mockMvc.perform(post("/card").contentType(MediaType.APPLICATION_JSON).content(asJsonString(card)))
        .andExpect(status().isAccepted());
  }

  @Test
  @DisplayName("POST /card Not Added")
  void testSaveCardNotAdded() throws Exception {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber("1234567890123456").expiryMonth(12).expiryYear(99).cvv(123)
        .cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    Card mockCard = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber(DigestUtils.shaHex("1234567890123456"))
        .hashedCardNumber(hashCardNumber("1234567890123456")).expiryMonth(12).expiryYear(99)
        .cvv(123).cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(false).when(cardService).addCard(card);
    mockMvc.perform(post("/card").contentType(MediaType.APPLICATION_JSON).content(asJsonString(card)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /card Deleted")
  void testSaveCardDeleted() throws Exception {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber(DigestUtils.shaHex("1234567890123456"))
        .hashedCardNumber(hashCardNumber("1234567890123456")).expiryMonth(12).expiryYear(99)
        .cvv(123).cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(true).when(cardService).deleteCard(card);
    mockMvc.perform(delete("/card").contentType(MediaType.APPLICATION_JSON).content(asJsonString(card)))
        .andExpect(status().isAccepted());
  }

  @Test
  @DisplayName("POST /card Not Deleted")
  void testSaveCardNotDeleted() throws Exception {
    Card card = Card.builder().userId(1L).cardHolderName("Srinivas Giduthuri")
        .cardNumber(DigestUtils.shaHex("1234567890123456"))
        .hashedCardNumber(hashCardNumber("1234567890123456")).expiryMonth(12).expiryYear(99)
        .cvv(123).cardIssuer(CardIssuer.MASTERCARD).expired(false).build();
    doReturn(false).when(cardService).deleteCard(card);
    mockMvc.perform(delete("/card").contentType(MediaType.APPLICATION_JSON).content(asJsonString(card)))
        .andExpect(status().isBadRequest());
  }

  private String hashCardNumber(String cardNumber) {
    String substring = StringUtils.substring(cardNumber, 0, cardNumber.length() - 4);
    String hash = StringUtils.repeat("X", substring.length());
    return StringUtils.replace(cardNumber, substring, hash);
  }

  private String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }
}
