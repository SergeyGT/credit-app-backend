package credit_app_back.app.service;

import credit_app_back.app.dto.*;
import credit_app_back.app.entity.Client;
import credit_app_back.app.entity.CreditApplication;
import credit_app_back.app.entity.CreditApplicationStatus;
import credit_app_back.app.entity.CreditAgreement;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.exception.MismatchClientDataException;
import credit_app_back.app.exception.logic.*;
import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.mapper.CreditAgreementMapper;
import credit_app_back.app.mapper.CreditApplicationMapper;
import credit_app_back.app.repository.ClientRepository;
import credit_app_back.app.repository.CreditApplicationRepository;
import credit_app_back.app.repository.CreditAgreementRepository;
import credit_app_back.app.util.ClientValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static credit_app_back.app.entity.CreditAgreeStatus.SIGNED;
import static credit_app_back.app.entity.CreditApplicationStatus.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditApplicationService {

    private final ClientValidator clientValidator;

    private final ClientMapper clientMapper;
    private final CreditApplicationMapper creditApplicationMapper;
    private final CreditAgreementMapper creditAgreementMapper;

    private final ICreditAnalyticService analyticService;

    private final ClientRepository clientRepository;
    private final CreditApplicationRepository creditApplicationRepository;
    private final CreditAgreementRepository creditAgreementRepository;

    @Value("${app.application.page-size:10}")
    private int pageSize;

    @Transactional
    public CreditApplicationDto createCreditApplication(CreateCreditApplicationRequestDto request) {
        log.debug("Creating credit application: {}", request);

        // 1. Валидация DTO
        Optional<GroupValidationException> validationExceptions =
                clientValidator.validateCreateCreditApplicationRequest(request);
        if (validationExceptions.isPresent()) {
            throw validationExceptions.get();
        }

        ClientDto clientData = request.getClient();
        Client client = getOrCreateClient(clientData);

        checkClientData(client, clientData);

        CreditApplication creditApplication = CreditApplication.builder()
                .client(client)
                .status(CreditApplicationStatus.PENDING)
                .requestedMoney(request.getDesiredLoanAmount())
                .loanPurpose(clientData.getLoanPurpose())
                .build();

        return processApplication(creditApplication);
    }

    private Client getOrCreateClient(ClientDto clientData) {
        log.debug("Getting or creating client with passport: {}", clientData.getPassport());

        Optional<Client> optionalClient = clientRepository.findByPassport(clientData.getPassport());

        if (optionalClient.isPresent()) {
            log.debug("Client found with passport: {}", clientData.getPassport());
            return optionalClient.get();
        }

        log.debug("Client not found, creating new client with passport: {}", clientData.getPassport());
        return clientRepository.save(clientMapper.toClient(clientData));
    }

    private void checkClientData(Client client, ClientDto clientDto) {
        log.debug("Checking client data for passport: {}", client.getPassport());

        if (!client.getFirstName().equals(clientDto.getFirstName()) ||
            !client.getLastName().equals(clientDto.getLastName()) ||
            !client.getPhone().equals(clientDto.getPhone())) {
            log.warn("Client data mismatch for passport: {}", client.getPassport());
            throw new MismatchClientDataException();
        }
    }

    private CreditApplicationDto processApplication(CreditApplication application) {
        CreditApplication savedApplication = creditApplicationRepository.save(application);
        log.debug("Application saved with id: {}", savedApplication.getId());

        analyticService.processCreditApplication(savedApplication.getId());

        CreditApplication processedApplication = creditApplicationRepository.findById(savedApplication.getId())
                .orElseThrow(() -> new ApplicationNotFoundException(savedApplication.getId()));

        log.info("Application {} processed with status: {}", 
                processedApplication.getId(), processedApplication.getStatus());

        return creditApplicationMapper.toDto(processedApplication);
    }

    @Transactional
    public CreditAgreementDto signCreditAgreement(Long applicationId) {
        log.debug("Signing agreement for application: {}", applicationId);

        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        checkIfCanSign(application);

        // 3. Подписываем договор
        CreditAgreement agreement = application.getCreditAgreement();

        if (agreement == null) {
            log.warn("Agreement not found for application: {}", applicationId);
            throw new AgreementNotFoundException(applicationId);
        }

        agreement.setSignStatus(SIGNED);
        agreement.setSignDate(LocalDate.now());

        application.setStatus(CreditApplicationStatus.SIGNED);

        creditAgreementRepository.save(agreement);
        creditApplicationRepository.save(application);

        log.info("Agreement signed for application: {}", applicationId);

        return creditAgreementMapper.toDto(agreement);
    }

    private void checkIfCanSign(CreditApplication application) {
        if (application.getStatus() != APPROVED) {
            log.warn("Application {} is not approved, status: {}", application.getId(), application.getStatus());
            throw new ApplicationCanNotSignException(application.getId(), application.getStatus());
        }

        CreditAgreement agreement = application.getCreditAgreement();
        if (agreement == null) {
            log.warn("Agreement not found for application: {}", application.getId());
            throw new AgreementNotFoundException(application.getId());
        }

        if (agreement.getSignStatus() == SIGNED) {
            log.warn("Agreement already signed for application: {}", application.getId());
            throw new AgreementAlreadySignedException(application.getId());
        }
    }

    public PageResponseDto<CreditApplicationDto> getApprovedApplications(int page) {
        log.debug("Getting approved applications, page: {}", page);

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<CreditApplication> applicationPage =
                creditApplicationRepository.findByStatus(CreditApplicationStatus.APPROVED, pageRequest);

        List<CreditApplicationDto> applicationDtos = applicationPage.getContent().stream()
                .map(creditApplicationMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalElements(),
                applicationDtos
        );
    }

    public CreditApplicationDto getApplicationById(Long id) {
        log.debug("Getting application by id: {}", id);
        CreditApplication application = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        return creditApplicationMapper.toDto(application);
    }

    public PageResponseDto<CreditApplicationDto> getAllApplications(int page) {
        log.debug("Getting all applications, page: {}", page);

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<CreditApplication> applicationPage = creditApplicationRepository.findAll(pageRequest);

        List<CreditApplicationDto> applicationDtos = applicationPage.getContent().stream()
                .map(creditApplicationMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalElements(),
                applicationDtos
        );
    }

    public PageResponseDto<CreditApplicationDto> getApplicationsByStatus(int page, CreditApplicationStatus status) {
        log.debug("Getting applications by status: {}, page: {}", status, page);

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<CreditApplication> applicationPage;

        if (status == null) {
            applicationPage = creditApplicationRepository.findAll(pageRequest);
        } else {
            applicationPage = creditApplicationRepository.findByStatus(status, pageRequest);
        }

        List<CreditApplicationDto> applicationDtos = applicationPage.getContent().stream()
                .map(creditApplicationMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalElements(),
                applicationDtos
        );
    }
}