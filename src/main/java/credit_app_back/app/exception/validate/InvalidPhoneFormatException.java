package credit_app_back.app.exception.validate;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class InvalidPhoneFormatException extends ValidationException {

    public InvalidPhoneFormatException(String errorDescription) {
        super(
                BaseExceptionCode.INVALID_PHONE_FORMAT,
                "Phone error: " + errorDescription
        );
    }
}