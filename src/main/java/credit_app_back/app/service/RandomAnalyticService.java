package credit_app_back.app.service;


import credit_app_back.app.entity.CreditAgreement;
import credit_app_back.app.entity.CreditApplication;
import credit_app_back.app.entity.CreditApplicationStatus;
import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.exception.logic.ApplicationAlreadyProcessedException;
import credit_app_back.app.exception.logic.ApplicationNotFoundException;
import credit_app_back.app.repository.CreditAgreementRepository;
import credit_app_back.app.repository.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static credit_app_back.app.entity.CreditApplicationStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomAnalyticService implements ICreditAnalyticService {

    private final CreditApplicationRepository creditApplicationRepository;
    private final CreditAgreementRepository creditAgreementRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void processCreditApplication(Long applicationId) {
        log.debug("Processing credit application: {}", applicationId);

        CreditApplication application = getApplication(applicationId);

        checkIfCanProcess(application);

        fillWithRandomValues(application);
    }

    private CreditApplication getApplication(Long id) {
        Optional<CreditApplication> optApplication = creditApplicationRepository.findById(id);
        return optApplication.orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    private void checkIfCanProcess(CreditApplication application) {
        if (application.getStatus() != PENDING) {
            log.warn("Application {} already processed, status: {}", application.getId(), application.getStatus());
            throw new ApplicationAlreadyProcessedException(application.getId());
        }
    }

    private void fillWithRandomValues(CreditApplication application) {
        // 1. Случайное решение (50/50)
        boolean isApproved = approveApplication();
        log.debug("Application {} decision: {}", application.getId(), isApproved ? "APPROVED" : "REJECTED");

        CreditApplicationStatus status = isApproved
                ? CreditApplicationStatus.APPROVED
                : CreditApplicationStatus.REJECTED;

        application.setStatus(status);

        // 2. Если отклонено — выходим
        if (status == CreditApplicationStatus.REJECTED) {
            log.debug("Application {} rejected", application.getId());
            return;
        }

        Integer approvedTerm = random.nextInt(30, 366);
        log.debug("Application {} approved term: {} days", application.getId(), approvedTerm);

        BigDecimal randomMultiplier = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextDouble(0.5, 1.0)
        );
        BigDecimal approvedMoney = application.getRequestedMoney()
                .multiply(randomMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
        log.debug("Application {} approved money: {}", application.getId(), approvedMoney);

        application.setApprovedTerm(approvedTerm);
        application.setApprovedMoney(approvedMoney);

        createAgreement(application);
    }

    private boolean approveApplication() {
        return random.nextBoolean();
    }

    private void createAgreement(CreditApplication application) {
        log.debug("Creating agreement for application: {}", application.getId());

        CreditAgreement agreement = CreditAgreement.builder()
                .creditApplication(application)
                .signStatus(CreditAgreeStatus.NOT_SIGNED)
                .signDate(null)
                .build();

        application.setCreditAgreement(agreement);
        creditApplicationRepository.save(application);

        log.info("Agreement created for application: {}", application.getId());
    }
}
