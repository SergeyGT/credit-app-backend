package credit_app_back.app.service;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.dto.CreditApplicationDto;
import credit_app_back.app.dto.CreateCreditApplicationRequestDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.Client;
import credit_app_back.app.entity.CreditApplication;
import credit_app_back.app.entity.CreditApplicationStatus;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.exception.MismatchClientDataException;
import credit_app_back.app.exception.logic.ApplicationNotFoundException;
import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.mapper.CreditAgreementMapper;
import credit_app_back.app.mapper.CreditApplicationMapper;
import credit_app_back.app.repository.ClientRepository;
import credit_app_back.app.repository.CreditApplicationRepository;
import credit_app_back.app.repository.CreditAgreementRepository;
import credit_app_back.app.util.ClientValidator;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @Mock
    private ClientValidator clientValidator;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CreditApplicationMapper creditApplicationMapper;

    @Mock
    private CreditAgreementMapper creditAgreementMapper;

    @Mock
    private ICreditAnalyticService analyticService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CreditApplicationRepository creditApplicationRepository;

    @Mock
    private CreditAgreementRepository creditAgreementRepository;

    @InjectMocks
    private CreditApplicationService creditApplicationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(creditApplicationService, "pageSize", 10);
    }

    @Test
    void createCreditApplication_existingClient_returnsDto() {
        ClientDto ClientDto = new ClientDto();
        ClientDto.setFirstName("Иван");
        ClientDto.setLastName("Петров");
        ClientDto.setPhone("+79995554433");
        ClientDto.setPassport("9876543210");
        ClientDto.setLoanPurpose("Покупка автомобиля");

        CreateCreditApplicationRequestDto request = new CreateCreditApplicationRequestDto();
        request.setClient(ClientDto);
        request.setDesiredLoanAmount(BigDecimal.valueOf(150000));

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setFirstName(ClientDto.getFirstName());
        existingClient.setLastName(ClientDto.getLastName());
        existingClient.setPhone(ClientDto.getPhone());
        existingClient.setPassport(ClientDto.getPassport());

        CreditApplication savedApplication = new CreditApplication();
        savedApplication.setId(1L);
        savedApplication.setClient(existingClient);
        savedApplication.setStatus(CreditApplicationStatus.PENDING);
        savedApplication.setRequestedMoney(request.getDesiredLoanAmount());
        savedApplication.setLoanPurpose(ClientDto.getLoanPurpose());

        CreditApplicationDto expectedDto = new CreditApplicationDto();

        when(clientValidator.validateCreateCreditApplicationRequest(request)).thenReturn(Optional.empty());
        when(clientRepository.findByPassport(ClientDto.getPassport())).thenReturn(Optional.of(existingClient));
        when(creditApplicationRepository.save(any(CreditApplication.class))).thenReturn(savedApplication);
        when(creditApplicationRepository.findById(1L)).thenReturn(Optional.of(savedApplication));
        when(creditApplicationMapper.toDto(savedApplication)).thenReturn(expectedDto);

        CreditApplicationDto result = creditApplicationService.createCreditApplication(request);

        assertThat(result).isSameAs(expectedDto);
        verify(analyticService).processCreditApplication(1L);
    }

    @Test
    void getApprovedApplications_returnsPagedResponse() {
        CreditApplication application = new CreditApplication();
        Page<CreditApplication> page = new PageImpl<>(List.of(application), PageRequest.of(0, 10), 1);

        when(creditApplicationRepository.findByStatus(CreditApplicationStatus.APPROVED, PageRequest.of(0, 10)))
                .thenReturn(page);

        CreditApplicationDto dto = new CreditApplicationDto();
        when(creditApplicationMapper.toDto(application)).thenReturn(dto);

        PageResponseDto<CreditApplicationDto> result = creditApplicationService.getApprovedApplications(0);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getData()).containsExactly(dto);
    }
}
