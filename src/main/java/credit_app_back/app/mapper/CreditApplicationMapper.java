package credit_app_back.app.mapper;

import credit_app_back.app.dto.CreditApplicationDto;
import credit_app_back.app.entity.CreditApplication;
import org.springframework.stereotype.Component;

@Component
public class CreditApplicationMapper {

    public CreditApplicationDto toDto(CreditApplication entity) {
        if (entity == null) {
            return null;
        }

        return CreditApplicationDto.builder()
                .applicationId(entity.getId())                    
                .clientId(entity.getClient().getId())             
                .clientFullName(                                  
                        entity.getClient().getFirstName() + " " +
                        entity.getClient().getLastName()
                )
                .status(entity.getStatus())                       
                .requestedMoney(entity.getRequestedMoney())       
                .approvedMoney(entity.getApprovedMoney())         
                .approvedTerm(entity.getApprovedTerm())           
                .loanPurpose(entity.getLoanPurpose())             
                .createdAt(entity.getCreatedAt())                 
                .decisionDate(entity.getDecisionDate())           
                .updatedAt(entity.getUpdatedAt())                 
                .build();
    }
}