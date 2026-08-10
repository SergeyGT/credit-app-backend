package credit_app_back.app.mapper;

import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.entity.CreditAgreement;
import org.springframework.stereotype.Component;

@Component
public class CreditAgreementMapper {

    public CreditAgreementDto toDto(CreditAgreement entity) {
        if (entity == null) {
            return null;
        }

        return CreditAgreementDto.builder()
                .id(entity.getId())
                .applicationId(entity.getCreditApplication().getId())
                .clientId(entity.getCreditApplication().getClient().getId())
                .clientFullName(
                        entity.getCreditApplication().getClient().getFirstName() + " " +
                        entity.getCreditApplication().getClient().getLastName()
                )
                .loanAmount(entity.getCreditApplication().getApprovedMoney())  
                .termDays(entity.getCreditApplication().getApprovedTerm())   
                .signDate(entity.getSignDate())
                .signStatus(entity.getSignStatus())
                .build();
    }
}