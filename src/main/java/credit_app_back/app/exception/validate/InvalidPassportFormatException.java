package credit_app_back.app.exception.validate;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class InvalidPassportFormatException extends ValidationException {

    public InvalidPassportFormatException(String errorDescription) {
        super(
                BaseExceptionCode.INVALID_PASSPORT_FORMAT,
                "Passport error: " + errorDescription
        );
    }
}