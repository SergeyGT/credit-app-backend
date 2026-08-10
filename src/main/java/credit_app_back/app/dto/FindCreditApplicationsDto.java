package credit_app_back.app.dto;

import credit_app_back.app.entity.CreditApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindCreditApplicationsDto implements Filterable {

    private CreditApplicationStatus status;  
    private Long clientId;                   
    private LocalDateTime fromDate;       
    private LocalDateTime toDate;           
}