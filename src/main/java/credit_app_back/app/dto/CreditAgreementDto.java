package credit_app_back.app.dto;

import java.time.LocalDate;

import credit_app_back.app.entity.CreditAgreeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditAgreementDto {
    private Long id;
    private Long applicationId;
    private LocalDate signDate;
    private CreditAgreeStatus signStatus;
}
