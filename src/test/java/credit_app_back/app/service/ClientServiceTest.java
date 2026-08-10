package credit_app_back.app.service;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.dto.FindClientsDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.Client;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.repository.ClientRepository;
import credit_app_back.app.util.ClientValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ClientValidator clientValidator;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clientService, "pageSize", 10);
    }

    @Test
    void getAllClients_returnsPagedResponse() {
        Client client = new Client();
        client.setFirstName("Иван");
        client.setLastName("Петров");
        client.setPassport("9876543210");

        Page<Client> clientPage = new PageImpl<>(List.of(client), PageRequest.of(0, 10), 1);
        when(clientRepository.findAll(PageRequest.of(0, 10))).thenReturn(clientPage);

        ClientDto dto = new ClientDto();
        dto.setFirstName("Иван");
        dto.setLastName("Петров");
        dto.setPassport("9876543210");
        when(clientMapper.toDto(client)).thenReturn(dto);

        PageResponseDto<ClientDto> result = clientService.getAllClients(0);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
        assertThat(result.getPage()).isEqualTo(0);
    }

    @Test
    void findClients_withoutFilters_fallsBackToGetAllClients() {
        FindClientsDto filters = new FindClientsDto();
        when(clientValidator.validateFindClientsDto(filters)).thenReturn(Optional.empty());

        Client client = new Client();
        client.setFirstName("Иван");
        client.setLastName("Петров");

        Page<Client> clientPage = new PageImpl<>(List.of(client), PageRequest.of(0, 10), 1);
        when(clientRepository.findAll(PageRequest.of(0, 10))).thenReturn(clientPage);

        ClientDto dto = new ClientDto();
        dto.setFirstName("Иван");
        dto.setLastName("Петров");
        when(clientMapper.toDto(client)).thenReturn(dto);

        PageResponseDto<ClientDto> result = clientService.findClients(0, filters);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
    }

    @Test
    void findClients_byLastName_usesRepositoryMethod() {
        FindClientsDto filters = new FindClientsDto();
        filters.setLastName("Козлов");
        when(clientValidator.validateFindClientsDto(filters)).thenReturn(Optional.empty());

        Client client = new Client();
        client.setFirstName("Иван");
        client.setLastName("Козлов");

        Page<Client> clientPage = new PageImpl<>(List.of(client), PageRequest.of(0, 10), 1);
        when(clientRepository.findClientsByFilters(
            isNull(), eq("%Козлов%"), isNull(), isNull(), isNull(), isNull(), any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(clientPage);

        ClientDto dto = new ClientDto();
        dto.setFirstName("Иван");
        dto.setLastName("Козлов");
        when(clientMapper.toDto(client)).thenReturn(dto);

        PageResponseDto<ClientDto> result = clientService.findClients(0, filters);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
    }

    @Test
    void createClient_savesClientWhenNoDuplicatesFound() {
        ClientDto dto = new ClientDto();
        dto.setFirstName("Иван");
        dto.setLastName("Петров");
        dto.setPassport("9876543210");
        dto.setPhone("+79995554433");

        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setPassport(dto.getPassport());
        client.setPhone(dto.getPhone());

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setFirstName(dto.getFirstName());
        savedClient.setLastName(dto.getLastName());
        savedClient.setPassport(dto.getPassport());
        savedClient.setPhone(dto.getPhone());

        when(clientValidator.validateClientDto(dto)).thenReturn(Optional.empty());
        when(clientRepository.findByPassport(dto.getPassport())).thenReturn(Optional.empty());
        when(clientRepository.findByPhone(dto.getPhone())).thenReturn(Optional.empty());
        when(clientMapper.toClient(dto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(savedClient);

        Client result = clientService.createClient(dto);

        assertThat(result).isSameAs(savedClient);
        verify(clientRepository).save(client);
    }

    @Test
    void getOrCreateClient_returnsExistingClientWhenPassportAlreadyExists() {
        ClientDto dto = new ClientDto();
        dto.setFirstName("Иван");
        dto.setLastName("Петров");
        dto.setPhone("+79995554433");
        dto.setPassport("9876543210");

        Client existingClient = new Client();
        existingClient.setFirstName(dto.getFirstName());
        existingClient.setLastName(dto.getLastName());
        existingClient.setPhone(dto.getPhone());
        existingClient.setPassport(dto.getPassport());

        when(clientValidator.validateClientDto(dto)).thenReturn(Optional.empty());
        when(clientRepository.findByPassport(dto.getPassport())).thenReturn(Optional.of(existingClient));

        Client result = clientService.getOrCreateClient(dto);

        assertThat(result).isSameAs(existingClient);
        verify(clientRepository, never()).save(any());
    }
}
