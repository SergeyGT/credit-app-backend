package credit_app_back.app.service;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.dto.FindClientsDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.Client;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.exception.MismatchClientDataException;
import credit_app_back.app.exception.logic.ApplicationNotFoundException;
import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.repository.ClientRepository;
import credit_app_back.app.repository.Page;
import credit_app_back.app.repository.Pageable;
import credit_app_back.app.util.ClientValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientValidator clientValidator;

    @Value("${app.client.page-size:10}")
    private int pageSize;

    public PageResponseDto<ClientDto> getAllClients(int page) {
        log.debug("Getting all clients, page: {}", page);

        Pageable pageable = Pageable.of(page, pageSize);
        Page<Client> clientPage = clientRepository.findAll(pageable);

        List<ClientDto> clientDtos = clientPage.getContent().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                clientPage.getPage(),
                clientPage.getPageSize(),
                clientPage.getTotal(),
                clientDtos
        );
    }

    public PageResponseDto<ClientDto> findClients(int page, FindClientsDto filters) {
        log.debug("Finding clients with filters: {}, page: {}", filters, page);

        Optional<GroupValidationException> validationExceptions =
                clientValidator.validateFindClientsDto(filters);
        if (validationExceptions.isPresent()) {
            throw validationExceptions.get();
        }

        String firstName = normalizeFilter(filters.getFirstName());
        String lastName = normalizeFilter(filters.getLastName());
        String middleName = normalizeFilter(filters.getMiddleName());
        String passport = normalizeFilter(filters.getPassport());
        String phone = normalizePhone(filters.getPhone());

        if (firstName == null && lastName == null && middleName == null &&
            passport == null && phone == null) {
            return getAllClients(page);
        }

        Pageable pageable = Pageable.of(page, pageSize);
        Page<Client> clientPage = clientRepository.findClientsByFilters(
                firstName,
                lastName,
                middleName,
                passport,
                phone,
                pageable
        );

        List<ClientDto> clientDtos = clientPage.getContent().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                clientPage.getPage(),
                clientPage.getPageSize(),
                clientPage.getTotal(),
                clientDtos
        );
    }

    private String normalizeFilter(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizePhone(String value) {
        String normalized = normalizeFilter(value);
        if (normalized == null) return null;
        return normalized.startsWith("+") ? normalized.substring(1) : normalized;
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public Optional<Client> getClientByPassport(String passport) {
        return clientRepository.findByPassport(passport);
    }

    public Optional<Client> getClientByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    @Transactional
    public Client createClient(ClientDto dto) {
        log.debug("Creating new client: {}", dto);

        Optional<GroupValidationException> validationResult =
                clientValidator.validateClientDto(dto);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }

        if (clientRepository.findByPassport(dto.getPassport()).isPresent()) {
            throw new MismatchClientDataException();
        }

        if (clientRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new MismatchClientDataException();
        }

        Client client = clientMapper.toClient(dto);
        Client savedClient = clientRepository.save(client);
        log.info("Client created with id: {}", savedClient.getId());

        return savedClient;
    }

    @Transactional
    public Client getOrCreateClient(ClientDto dto) {
        log.debug("Getting or creating client: {}", dto);

        Optional<GroupValidationException> validationResult =
                clientValidator.validateClientDto(dto);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }

        Optional<Client> existingClient = clientRepository.findByPassport(dto.getPassport());
        if (existingClient.isPresent()) {
            Client client = existingClient.get();
            if (!client.getFirstName().equals(dto.getFirstName()) ||
                !client.getLastName().equals(dto.getLastName()) ||
                !client.getPhone().equals(dto.getPhone())) {
                throw new MismatchClientDataException();
            }
            return client;
        }

        return createClient(dto);
    }
}