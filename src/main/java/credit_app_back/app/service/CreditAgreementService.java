package credit_app_back.app.service;

import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.CreditAgreement;
import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.exception.logic.AgreementNotFoundException;
import credit_app_back.app.mapper.CreditAgreementMapper;
import credit_app_back.app.repository.CreditAgreementRepository;
import credit_app_back.app.repository.Page;
import credit_app_back.app.repository.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static credit_app_back.app.entity.CreditAgreeStatus.SIGNED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditAgreementService {

    private final CreditAgreementRepository creditAgreementRepository;
    private final CreditAgreementMapper creditAgreementMapper;

    @Value("${app.agreement.page-size:10}")
    private int pageSize;
    
    public PageResponseDto<CreditAgreementDto> getSignedAgreements(int page) {
        log.debug("Getting signed agreements, page: {}", page);

        Pageable pageable = Pageable.of(page, pageSize);
        Page<CreditAgreement> agreementPage =
                creditAgreementRepository.findBySignStatus(SIGNED, pageable);

        List<CreditAgreementDto> agreementDtos = agreementPage.getContent().stream()
                .map(creditAgreementMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                agreementPage.getPage(),
                agreementPage.getPageSize(),
                agreementPage.getTotal(),
                agreementDtos
        );
    }

    public CreditAgreement getAgreementByApplicationId(Long applicationId) {
        log.debug("Getting agreement by application id: {}", applicationId);
        return creditAgreementRepository.findByCreditApplicationId(applicationId)
                .orElseThrow(() -> new AgreementNotFoundException(applicationId));
    }

    public CreditAgreementDto getAgreementById(Long id) {
        log.debug("Getting agreement by id: {}", id);
        CreditAgreement agreement = creditAgreementRepository.findById(id)
                .orElseThrow(() -> new AgreementNotFoundException(id));
        return creditAgreementMapper.toDto(agreement);
    }

    public PageResponseDto<CreditAgreementDto> getAllAgreements(int page) {
        log.debug("Getting all agreements, page: {}", page);

        Pageable pageable = Pageable.of(page, pageSize);
        Page<CreditAgreement> agreementPage = creditAgreementRepository.findAll(pageable);

        List<CreditAgreementDto> agreementDtos = agreementPage.getContent().stream()
                .map(creditAgreementMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                agreementPage.getPage(),
                agreementPage.getPageSize(),
                agreementPage.getTotal(),
                agreementDtos
        );
    }

    public PageResponseDto<CreditAgreementDto> findAgreementsByStatus(
            int page,
            CreditAgreeStatus status
    ) {
        log.debug("Finding agreements by status: {}, page: {}", status, page);

        Pageable pageable = Pageable.of(page, pageSize);
        Page<CreditAgreement> agreementPage;

        if (status == null) {
            agreementPage = creditAgreementRepository.findAll(pageable);
        } else {
            agreementPage = creditAgreementRepository.findBySignStatus(status, pageable);
        }

        List<CreditAgreementDto> agreementDtos = agreementPage.getContent().stream()
                .map(creditAgreementMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                agreementPage.getPage(),
                agreementPage.getPageSize(),
                agreementPage.getTotal(),
                agreementDtos
        );
    }
}