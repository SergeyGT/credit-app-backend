package credit_app_back.app.util;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.dto.CreateCreditApplicationRequestDto;
import credit_app_back.app.dto.FindClientsDto;
import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.exception.ValidationException;
import credit_app_back.app.exception.logic.InvalidNameFormatException;
import credit_app_back.app.exception.validate.InvalidPassportFormatException;
import credit_app_back.app.exception.validate.InvalidPhoneFormatException;
import credit_app_back.app.exception.validate.MissingPartEmploymentDataException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClientValidator {

    public Optional<GroupValidationException> validateCreateCreditApplicationRequest(
            CreateCreditApplicationRequestDto dto
    ) {
        List<ValidationException> exceptions = new ArrayList<>();

        validateClientData(dto.getClient(), exceptions);
        validateLoanAmount(dto.getDesiredLoanAmount(), exceptions);

        if (exceptions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new GroupValidationException(exceptions));
    }

    /**
     * Валидация DTO для поиска клиентов
     * Проверяем ТОЛЬКО те поля, которые переданы
     */
    public Optional<GroupValidationException> validateFindClientsDto(FindClientsDto dto) {
        List<ValidationException> exceptions = new ArrayList<>();

        // Если все поля null — пропускаем
        if (dto.getFirstName() == null && dto.getLastName() == null &&
            dto.getMiddleName() == null && dto.getPassport() == null &&
            dto.getPhone() == null) {
            return Optional.empty();
        }

        // Проверяем только переданные поля
        if (dto.getFirstName() != null) {
            validatePartOfName("firstName", dto.getFirstName(), exceptions);
        }
        if (dto.getLastName() != null) {
            validatePartOfName("lastName", dto.getLastName(), exceptions);
        }
        if (dto.getMiddleName() != null) {
            validatePartOfName("middleName", dto.getMiddleName(), exceptions);
        }
        if (dto.getPassport() != null) {
            validatePassport(dto.getPassport(), exceptions);
        }
        if (dto.getPhone() != null) {
            validatePhone(dto.getPhone(), exceptions);
        }

        if (exceptions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new GroupValidationException(exceptions));
    }

    public Optional<GroupValidationException> validateClientDto(ClientDto dto) {
        List<ValidationException> exceptions = new ArrayList<>();

        validateClientData(dto, exceptions);

        if (exceptions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new GroupValidationException(exceptions));
    }

    // ===== ПРИВАТНЫЕ МЕТОДЫ ВАЛИДАЦИИ =====

    private void validateClientData(ClientDto dto, List<ValidationException> exceptions) {
        validatePartOfName("firstName", dto.getFirstName(), exceptions);
        validatePartOfName("lastName", dto.getLastName(), exceptions);
        validatePartOfName("middleName", dto.getMiddleName(), exceptions);
        validatePassport(dto.getPassport(), exceptions);
        validatePhone(dto.getPhone(), exceptions);
        validateEmploymentData(dto, exceptions);
        validateAddresses(dto, exceptions);
        validateGender(dto, exceptions);
        validateFamilyStatus(dto, exceptions);
        validateLoanPurpose(dto, exceptions);
    }

    private void validatePartOfName(String fieldName, String name, List<ValidationException> exceptions) {
        // Для firstName и lastName — обязательные поля
        if (name == null || name.isBlank()) {
            if ("firstName".equals(fieldName) || "lastName".equals(fieldName)) {
                exceptions.add(new InvalidNameFormatException(fieldName, "is required"));
            }
            return;
        }

        if (name.length() > 64) {
            exceptions.add(new InvalidNameFormatException(fieldName, "must not exceed 64 characters"));
            return;
        }

        if (!name.matches("^[A-Za-zА-Яа-я][a-zа-я]*(?:[\\s'-][A-Za-zА-Яа-я][a-zа-я]*)*$")) {
            exceptions.add(new InvalidNameFormatException(
                fieldName, 
                "First letter uppercase, rest lowercase, spaces/hyphens/apostrophes allowed"
            ));
        }
    }

    private void validatePassport(String passport, List<ValidationException> exceptions) {
        if (passport == null || passport.isBlank()) {
            exceptions.add(new InvalidPassportFormatException("Passport is required"));
            return;
        }

        if (passport.length() != 10) {
            exceptions.add(new InvalidPassportFormatException("Passport must contain exactly 10 digits"));
            return;
        }

        if (!passport.matches("^\\d{10}$")) {
            exceptions.add(new InvalidPassportFormatException("Passport must contain only digits"));
        }
    }

    private void validatePhone(String phone, List<ValidationException> exceptions) {
        if (phone == null) {
            exceptions.add(new InvalidPhoneFormatException("Phone is required"));
            return;
        }

        String normalizedPhone = phone.trim();
        if (normalizedPhone.isEmpty()) {
            exceptions.add(new InvalidPhoneFormatException("Phone is required"));
            return;
        }

        if (normalizedPhone.length() < 10 || normalizedPhone.length() > 12) {
            exceptions.add(new InvalidPhoneFormatException("Phone must contain 10-12 characters"));
            return;
        }

        if (!normalizedPhone.matches("^\\+?\\d{10,12}$")) {
            exceptions.add(new InvalidPhoneFormatException("Invalid phone format. Expected: +xxxxxxxxxxx or xxxxxxxxxxx"));
        }
    }

    private void validateEmploymentData(ClientDto dto, List<ValidationException> exceptions) {
        LocalDate startDate = dto.getEmploymentStartDate();
        LocalDate endDate = dto.getEmploymentEndDate();
        String position = dto.getEmploymentPosition();
        String organization = dto.getOrganizationName();

        if (startDate == null && endDate == null &&
                (position == null || position.isBlank()) &&
                (organization == null || organization.isBlank())) {
            return;
        }

        if (startDate == null) {
            exceptions.add(new MissingPartEmploymentDataException("employmentStartDate"));
        }

        if (position == null || position.isBlank()) {
            exceptions.add(new MissingPartEmploymentDataException("employmentPosition"));
        }

        if (organization == null || organization.isBlank()) {
            exceptions.add(new MissingPartEmploymentDataException("organizationName"));
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            exceptions.add(new MissingPartEmploymentDataException("Employment end date must be after start date"));
        }
    }

    private void validateAddresses(ClientDto dto, List<ValidationException> exceptions) {
        if (dto.getResidenceAddress() == null || dto.getResidenceAddress().isBlank()) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }

        if (dto.getRegistrationAddress() == null || dto.getRegistrationAddress().isBlank()) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }
    }

    private void validateGender(ClientDto dto, List<ValidationException> exceptions) {
        if (dto.getGender() == null) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }
    }

    private void validateFamilyStatus(ClientDto dto, List<ValidationException> exceptions) {
        if (dto.getFamilyStatus() == null) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }
    }

    private void validateLoanPurpose(ClientDto dto, List<ValidationException> exceptions) {
        if (dto.getLoanPurpose() == null || dto.getLoanPurpose().isBlank()) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }
    }

    private void validateLoanAmount(BigDecimal amount, List<ValidationException> exceptions) {
        if (amount == null) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }

        if (amount.compareTo(new BigDecimal("10000000")) > 0) {
            exceptions.add(new ValidationException(null) {
                @Override
                public BaseExceptionCode getExceptionCode() {
                    return BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR;
                }
            });
        }
    }
}