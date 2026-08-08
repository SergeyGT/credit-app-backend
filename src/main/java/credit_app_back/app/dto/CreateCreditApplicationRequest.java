package credit_app_back.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCreditApplicationRequest {

    private ClientDto client;
    
    @NotNull
    @Positive
    @DecimalMin(value = "0.0", inclusive = false, message = "Desired loan amount must be greater than zero")
    @DecimalMax(value = "100000000.0", inclusive = true, message = "Desired loan amount exceeds the maximum limit of 100,000,000")
    private BigDecimal desiredLoanAmount;
}
