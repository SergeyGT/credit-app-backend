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

    /**
     * 1) Вывод списка всех клиентов с пагинацией
     */
    public PageResponseDto<ClientDto> getAllClients(int page) {
        log.debug("Getting all clients, page: {}", page);
        
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Client> clientPage = clientRepository.findAll(pageRequest);
        
        List<ClientDto> clientDtos = clientPage.getContent().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
        
        return new PageResponseDto<>(
                clientPage.getNumber(),
                clientPage.getSize(),
                clientPage.getTotalElements(),
                clientDtos
        );
    }

    /**
     * 2) Поиск клиента по телефону, ФИО и паспортным данным
     */
    public PageResponseDto<ClientDto> findClients(int page, FindClientsDto filters) {
        log.debug("Finding clients with filters: {}, page: {}", filters, page);
        
        // Валидация DTO
        Optional<GroupValidationException> validationExceptions = 
                clientValidator.validateFindClientsDto(filters);
        if (validationExceptions.isPresent()) {
            throw validationExceptions.get();
        }

        // Если все поля пустые — возвращаем всех клиентов
        if (filters.getFirstName() == null && filters.getLastName() == null &&
            filters.getMiddleName() == null && filters.getPassport() == null &&
            filters.getPhoneNumber() == null) {
            return getAllClients(page);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Client> clientPage = clientRepository.findClientsByFilters(
                filters.getFirstName(),
                filters.getLastName(),
                filters.getMiddleName(),
                filters.getPassport(),
                filters.getPhoneNumber(),
                pageRequest
        );

        List<ClientDto> clientDtos = clientPage.getContent().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                clientPage.getNumber(),
                clientPage.getSize(),
                clientPage.getTotalElements(),
                clientDtos
        );
    }

    /**
     * Получить клиента по ID
     */
    public Client getClientById(Long id) {
        log.debug("Getting client by id: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    /**
     * Получить клиента по паспорту
     */
    public Optional<Client> getClientByPassport(String passport) {
        log.debug("Getting client by passport: {}", passport);
        return clientRepository.findByPassport(passport);
    }

    /**
     * Получить клиента по телефону
     */
    public Optional<Client> getClientByPhone(String phone) {
        log.debug("Getting client by phone: {}", phone);
        return clientRepository.findByPhone(phone);
    }

    /**
     * Создать нового клиента
     */
    @Transactional
    public Client createClient(ClientDto dto) {
        log.debug("Creating new client: {}", dto);
        
        // 1. Валидация DTO
        Optional<GroupValidationException> validationResult = 
                clientValidator.validateClientDto(dto);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }

        // 2. Проверка на дубликат по паспорту
        if (clientRepository.findByPassport(dto.getPassport()).isPresent()) {
            throw new MismatchClientDataException();
        }

        // 3. Проверка на дубликат по телефону
        if (clientRepository.findByPhone(dto.getPhoneNumber()).isPresent()) {
            throw new MismatchClientDataException();
        }

        // 4. Сохранение
        Client client = clientMapper.toClient(dto);
        Client savedClient = clientRepository.save(client);
        log.info("Client created with id: {}", savedClient.getId());
        
        return savedClient;
    }
    
    /**
     * Получить или создать клиента (для оформления заявки)
     */
    @Transactional
    public Client getOrCreateClient(ClientDto dto) {
        log.debug("Getting or creating client: {}", dto);
        
        // 1. Валидация DTO
        Optional<GroupValidationException> validationResult = 
                clientValidator.validateClientDto(dto);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }
        
        // 2. Проверка: существует ли клиент с таким паспортом?
        Optional<Client> existingClient = clientRepository.findByPassport(dto.getPassport());
        if (existingClient.isPresent()) {
            Client client = existingClient.get();
            log.debug("Client found with passport: {}", dto.getPassport());
            
            // 3. Проверка: совпадают ли данные?
            if (!client.getFirstName().equals(dto.getFirstName()) ||
                !client.getLastName().equals(dto.getLastName()) ||
                !client.getPhone().equals(dto.getPhoneNumber())) {
                log.warn("Client data mismatch for passport: {}", dto.getPassport());
                throw new MismatchClientDataException();
            }
            
            return client;
        }
        
        // 4. Если клиента нет — создаем нового
        log.info("Client not found, creating new client with passport: {}", dto.getPassport());
        return createClient(dto);
    }
}