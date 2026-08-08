package credit_app_back.app.dto;

import java.math.BigDecimal;

import credit_app_back.app.entity.CreditApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditApplicationDto {
    private Long applicationId;
    private Integer approvedTerm;
    private BigDecimal approvedMoney;    
    private CreditApplicationStatus status;
}
