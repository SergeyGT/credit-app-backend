package credit_app_back.app.service;

import credit_app_back.app.dto.CreditAgreementDto;
import credit_app_back.app.entity.CreditAgreement;
import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.exception.logic.AgreementNotFoundException;
import credit_app_back.app.mapper.CreditAgreementMapper;
import credit_app_back.app.repository.CreditAgreementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditAgreementServiceTest {

    @Mock
    private CreditAgreementRepository creditAgreementRepository;

    @Mock
    private CreditAgreementMapper creditAgreementMapper;

    @InjectMocks
    private CreditAgreementService creditAgreementService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(creditAgreementService, "pageSize", 10);
    }

    @Test
    void getSignedAgreements_returnsPagedResponse() {
        CreditAgreement agreement = new CreditAgreement();
        agreement.setSignStatus(CreditAgreeStatus.SIGNED);

        Page<CreditAgreement> page = new PageImpl<>(List.of(agreement), PageRequest.of(0, 10), 1);
        when(creditAgreementRepository.findBySignStatus(CreditAgreeStatus.SIGNED, PageRequest.of(0, 10)))
                .thenReturn(page);

        CreditAgreementDto dto = new CreditAgreementDto();
        when(creditAgreementMapper.toDto(agreement)).thenReturn(dto);

        var result = creditAgreementService.getSignedAgreements(0);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
    }

    @Test
    void getAgreementByApplicationId_throwsWhenNotFound() {
        when(creditAgreementRepository.findByCreditApplicationId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditAgreementService.getAgreementByApplicationId(10L))
                .isInstanceOf(AgreementNotFoundException.class);
    }

    @Test
    void getAgreementById_returnsDto() {
        CreditAgreement agreement = new CreditAgreement();
        agreement.setId(5L);

        when(creditAgreementRepository.findById(5L)).thenReturn(Optional.of(agreement));

        CreditAgreementDto dto = new CreditAgreementDto();
        when(creditAgreementMapper.toDto(agreement)).thenReturn(dto);

        var result = creditAgreementService.getAgreementById(5L);

        assertThat(result).isSameAs(dto);
    }

    @Test
    void findAgreementsByStatus_withNullStatus_returnsAllAgreements() {
        CreditAgreement agreement = new CreditAgreement();
        Page<CreditAgreement> page = new PageImpl<>(List.of(agreement), PageRequest.of(0, 10), 1);
        when(creditAgreementRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        CreditAgreementDto dto = new CreditAgreementDto();
        when(creditAgreementMapper.toDto(agreement)).thenReturn(dto);

        var result = creditAgreementService.findAgreementsByStatus(0, null);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
        verify(creditAgreementRepository).findAll(PageRequest.of(0, 10));
    }
}
