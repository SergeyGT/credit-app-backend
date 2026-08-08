package credit_app_back.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import credit_app_back.app.dto.CreditApplicationDto;
import credit_app_back.app.entity.CreditApplication;

@Mapper(componentModel = "spring")
public interface CreditApplicationMapper {
    @Mapping(source = "id", target = "applicationId")
    CreditApplicationDto toDto(CreditApplication creditApplication);
    CreditApplication toCreditApplication(CreditApplicationDto creditApplicationDto);
}
