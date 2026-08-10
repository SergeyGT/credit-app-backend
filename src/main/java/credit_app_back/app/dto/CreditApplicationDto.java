package credit_app_back.app.dto;

import credit_app_back.app.entity.CreditApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationDto {
    private Long applicationId;          // ID заявки
    private Long clientId;               // ID клиента
    private String clientFullName;       // ФИО клиента
    private CreditApplicationStatus status;
    private BigDecimal requestedMoney;   // Запрошенная сумма
    private BigDecimal approvedMoney;    // Одобренная сумма
    private Integer approvedTerm;        // Срок
    private String loanPurpose;          // Цель кредита
    private LocalDateTime createdAt;
    private LocalDateTime decisionDate;
    private LocalDateTime updatedAt;
}