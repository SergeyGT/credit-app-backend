package credit_app_back.app.exception;

import org.springframework.validation.FieldError;

public class JakartaValidationException extends ValidationException {

    private final String field;
    private final String rejectedValue;

    public JakartaValidationException(FieldError fieldError) {
        super(
                BaseExceptionCode.DTO_FIELD_VALIDATION_ERROR,
                String.format("Field '%s' has invalid value '%s': %s",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                )
        );
        this.field = fieldError.getField();
        this.rejectedValue = String.valueOf(fieldError.getRejectedValue());
    }

    public String getField() { return field; }
    public String getRejectedValue() { return rejectedValue; }
}