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
import credit_app_back.app.util.ClientValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Client> clientPage = clientRepository.findAll(pageRequest);
        return toPageResponse(clientPage);
    }

    public PageResponseDto<ClientDto> findClients(int page, FindClientsDto filters) {
        log.debug("Finding clients with filters: {}, page: {}", filters, page);

        validateFilters(filters);

        String firstName = normalize(filters.getFirstName());
        String lastName = normalize(filters.getLastName());
        String middleName = normalize(filters.getMiddleName());
        String passport = normalize(filters.getPassport());
        String phone = normalizePhone(filters.getPhone());

        if (isEmptySearch(firstName, lastName, middleName, passport, phone)) {
            return getAllClients(page);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);

        if (passport != null) {
            return findExactByPassport(passport, page);
        }
        String firstNameLike = firstName == null ? null : "%" + firstName.toLowerCase() + "%";
        String lastNameLike = lastName == null ? null : "%" + lastName.toLowerCase() + "%";
        String middleNameLike = middleName == null ? null : "%" + middleName.toLowerCase() + "%";
        String phonePlus = phone == null ? null : (phone.startsWith("+") ? phone : "+" + phone);

        Page<Client> clientPage = clientRepository.findClientsByFilters(
            firstNameLike, lastNameLike, middleNameLike, passport, phone, phonePlus, pageRequest
        );

        return toPageResponse(clientPage);
    }

    private void validateFilters(FindClientsDto filters) {
        Optional<GroupValidationException> validationExceptions =
                clientValidator.validateFindClientsDto(filters);
        if (validationExceptions.isPresent()) {
            throw validationExceptions.get();
        }
    }

    private boolean isEmptySearch(String firstName, String lastName, String middleName,
                                  String passport, String phone) {
        return firstName == null && lastName == null && middleName == null &&
               passport == null && phone == null;
    }

    private PageResponseDto<ClientDto> findExactByPassport(String passport, int page) {
        Optional<Client> byPassport = clientRepository.findByPassport(passport);
        if (byPassport.isPresent()) {
            List<ClientDto> dtoList = List.of(clientMapper.toDto(byPassport.get()));
            return new PageResponseDto<>(page, pageSize, 1, dtoList);
        }
        return new PageResponseDto<>(page, pageSize, 0, List.of());
    }

    private PageResponseDto<ClientDto> toPageResponse(Page<Client> page) {
        List<ClientDto> dtos = page.getContent().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
        return new PageResponseDto<>(page.getNumber(), page.getSize(), page.getTotalElements(), dtos);
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizePhone(String value) {
        String normalized = normalize(value);
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
        validateClientDto(dto);
        checkDuplicate(dto);
        Client client = clientMapper.toClient(dto);
        Client savedClient = clientRepository.save(client);
        log.info("Client created with id: {}", savedClient.getId());
        return savedClient;
    }

    @Transactional
    public Client getOrCreateClient(ClientDto dto) {
        log.debug("Getting or creating client: {}", dto);
        validateClientDto(dto);

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

    private void validateClientDto(ClientDto dto) {
        Optional<GroupValidationException> validationResult =
                clientValidator.validateClientDto(dto);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }
    }

    private void checkDuplicate(ClientDto dto) {
        if (clientRepository.findByPassport(dto.getPassport()).isPresent()) {
            throw new MismatchClientDataException();
        }
        if (clientRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new MismatchClientDataException();
        }
    }
}