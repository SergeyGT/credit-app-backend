package credit_app_back.app.mapper;

import credit_app_back.app.dto.CreditApplicationDto;
import credit_app_back.app.entity.CreditApplication;
import org.springframework.stereotype.Component;

@Component
public class CreditApplicationMapper {

    public CreditApplicationDto toDto(CreditApplication entity) {
        if (entity == null) {
            return null;
        }

        return CreditApplicationDto.builder()
                .applicationId(entity.getId())                    // ← ID заявки
                .clientId(entity.getClient().getId())             // ← ID клиента
                .clientFullName(                                  // ← ФИО клиента
                        entity.getClient().getFirstName() + " " +
                        entity.getClient().getLastName()
                )
                .status(entity.getStatus())                       // ← Статус
                .requestedMoney(entity.getRequestedMoney())       // ← Запрошенная сумма
                .approvedMoney(entity.getApprovedMoney())         // ← Одобренная сумма
                .approvedTerm(entity.getApprovedTerm())           // ← Срок
                .loanPurpose(entity.getLoanPurpose())             // ← Цель кредита
                .createdAt(entity.getCreatedAt())                 // ← Дата создания
                .decisionDate(entity.getDecisionDate())           // ← Дата решения
                .updatedAt(entity.getUpdatedAt())                 // ← Дата обновления
                .build();
    }
}