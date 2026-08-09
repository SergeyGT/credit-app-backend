package credit_app_back.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.entity.CreditAgreement;

@Mapper(componentModel = "spring")
public interface CreditAgreementMapper {

    @Mapping(target = "applicationId", source = "creditApplication.id")
    CreditAgreementDto toDto(CreditAgreement agreement);

}