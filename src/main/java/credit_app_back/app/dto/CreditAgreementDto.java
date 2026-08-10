package credit_app_back.app.dto;

import credit_app_back.app.entity.CreditAgreeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAgreementDto {
    private Long id;
    private Long applicationId;
    private Long clientId;
    private String clientFullName;
    private BigDecimal loanAmount;
    private Integer termDays;
    private LocalDate signDate;
    private CreditAgreeStatus signStatus;
}