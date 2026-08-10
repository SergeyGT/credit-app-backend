package credit_app_back.app.mapper;

import org.mapstruct.Mapper;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDto toDto(Client client);
    Client toClient(ClientDto ClientDto);
}