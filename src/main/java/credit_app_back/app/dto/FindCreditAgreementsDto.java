package credit_app_back.app.dto;

import credit_app_back.app.entity.CreditAgreeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindCreditAgreementsDto implements Filterable {

    private CreditAgreeStatus signStatus;   
    private Long clientId;                 
    private LocalDate fromDate;            
    private LocalDate toDate;               
}