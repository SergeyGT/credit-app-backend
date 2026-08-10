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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final EntityManager entityManager;

    @Value("${app.client.page-size:10}")
    private int pageSize;

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

    public PageResponseDto<ClientDto> findClients(int page, FindClientsDto filters) {
        log.debug("Finding clients with filters: {}, page: {}", filters, page);
        
        Optional<GroupValidationException> validationExceptions = 
                clientValidator.validateFindClientsDto(filters);
        if (validationExceptions.isPresent()) {
            throw validationExceptions.get();
        }

        if (filters.getFirstName() == null && filters.getLastName() == null &&
            filters.getMiddleName() == null && filters.getPassport() == null &&
            filters.getPhone() == null) {
            return getAllClients(page);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Client> selectQuery = cb.createQuery(Client.class);
        Root<Client> root = selectQuery.from(Client.class);
        List<Predicate> predicates = buildClientPredicates(cb, root, filters);
        selectQuery.select(root).where(predicates.toArray(new Predicate[0]));

        TypedQuery<Client> typedQuery = entityManager.createQuery(selectQuery);
        typedQuery.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        typedQuery.setMaxResults(pageRequest.getPageSize());
        List<Client> clients = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Client> countRoot = countQuery.from(Client.class);
        countQuery.select(cb.count(countRoot))
                .where(buildClientPredicates(cb, countRoot, filters).toArray(new Predicate[0]));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        List<ClientDto> clientDtos = clients.stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                total,
                clientDtos
        );
    }

    private List<Predicate> buildClientPredicates(
            CriteriaBuilder cb,
            Root<Client> root,
            FindClientsDto filters
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.getFirstName() != null) {
            predicates.add(cb.like(
                    cb.lower(root.get("firstName")),
                    "%" + filters.getFirstName().toLowerCase() + "%"
            ));
        }

        if (filters.getLastName() != null) {
            predicates.add(cb.like(
                    cb.lower(root.get("lastName")),
                    "%" + filters.getLastName().toLowerCase() + "%"
            ));
        }

        if (filters.getMiddleName() != null) {
            predicates.add(cb.like(
                    cb.lower(root.get("middleName")),
                    "%" + filters.getMiddleName().toLowerCase() + "%"
            ));
        }

        if (filters.getPassport() != null) {
            predicates.add(cb.equal(root.get("passport"), filters.getPassport()));
        }

        if (filters.getPhone() != null) {
            predicates.add(cb.equal(root.get("phone"), filters.getPhone()));
        }

        return predicates;
    }

    public Client getClientById(Long id) {
        log.debug("Getting client by id: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public Optional<Client> getClientByPassport(String passport) {
        log.debug("Getting client by passport: {}", passport);
        return clientRepository.findByPassport(passport);
    }

    public Optional<Client> getClientByPhone(String phone) {
        log.debug("Getting client by phone: {}", phone);
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
            log.debug("Client found with passport: {}", dto.getPassport());
            
            // 3. Проверка: совпадают ли данные?
            if (!client.getFirstName().equals(dto.getFirstName()) ||
                !client.getLastName().equals(dto.getLastName()) ||
                !client.getPhone().equals(dto.getPhone())) {
                log.warn("Client data mismatch for passport: {}", dto.getPassport());
                throw new MismatchClientDataException();
            }
            
            return client;
        }
        
        log.info("Client not found, creating new client with passport: {}", dto.getPassport());
        return createClient(dto);
    }
}