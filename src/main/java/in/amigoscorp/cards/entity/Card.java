package in.amigoscorp.cards.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Serializable {

  private Long userId;
  private String cardHolderName;
  @Id
  private String cardNumber;
  private String hashedCardNumber;
  private Integer expiryMonth;
  private Integer expiryYear;
  @JsonInclude(Include.NON_NULL)
  @Transient
  private transient Integer cvv;
  private CardIssuer cardIssuer;
  private boolean expired;
}
