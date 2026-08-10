package credit_app_back.app.controller;


import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.service.CreditAgreementService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/agreements")
@RequiredArgsConstructor
public class CreditAgreementController {

    private final CreditAgreementService creditAgreementService;

    @GetMapping("/signed")
    public ResponseEntity<PageResponseDto<CreditAgreementDto>> getSignedAgreements(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/agreements/signed, page: {}", page);
        PageResponseDto<CreditAgreementDto> response = 
                creditAgreementService.getSignedAgreements(page);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<CreditAgreementDto>> getAllAgreements(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/agreements, page: {}", page);
        PageResponseDto<CreditAgreementDto> response = 
                creditAgreementService.getAllAgreements(page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditAgreementDto> getAgreementById(
            @PathVariable Long id
    ) {
        log.debug("GET /api/agreements/{}", id);
        CreditAgreementDto response = creditAgreementService.getAgreementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<PageResponseDto<CreditAgreementDto>> getAgreementsByStatus(
            @RequestParam CreditAgreeStatus status,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page
    ) {
        log.debug("GET /api/agreements/status, status: {}, page: {}", status, page);
        PageResponseDto<CreditAgreementDto> response = 
                creditAgreementService.findAgreementsByStatus(page, status);
        return ResponseEntity.ok(response);
    }
}