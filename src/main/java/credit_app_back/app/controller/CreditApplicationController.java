package credit_app_back.app.controller;

import credit_app_back.app.dto.CreateCreditApplicationRequestDto;
import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.dto.CreditApplicationDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.CreditApplicationStatus;
import credit_app_back.app.service.CreditApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;

    @PostMapping
    public ResponseEntity<CreditApplicationDto> createApplication(
            @Valid @RequestBody CreateCreditApplicationRequestDto request
    ) {
        log.debug("POST /api/applications, request: {}", request);
        CreditApplicationDto response = creditApplicationService.createCreditApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/sign")
    public ResponseEntity<CreditAgreementDto> signAgreement(
            @PathVariable Long id
    ) {
        log.debug("PATCH /api/applications/{}/sign", id);
        CreditAgreementDto response = creditApplicationService.signCreditAgreement(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    public ResponseEntity<PageResponseDto<CreditApplicationDto>> getApprovedApplications(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/applications/approved, page: {}", page);
        PageResponseDto<CreditApplicationDto> response = creditApplicationService.getApprovedApplications(page);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<CreditApplicationDto>> getAllApplications(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/applications, page: {}", page);
        PageResponseDto<CreditApplicationDto> response = creditApplicationService.getAllApplications(page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditApplicationDto> getApplicationById(
            @PathVariable Long id
    ) {
        log.debug("GET /api/applications/{}", id);
        CreditApplicationDto response = creditApplicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<PageResponseDto<CreditApplicationDto>> getApplicationsByStatus(
            @RequestParam CreditApplicationStatus status,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/applications/status, status: {}, page: {}", status, page);
        PageResponseDto<CreditApplicationDto> response = 
                creditApplicationService.getApplicationsByStatus(page, status);
        return ResponseEntity.ok(response);
    }
}