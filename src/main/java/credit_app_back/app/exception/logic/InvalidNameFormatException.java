package credit_app_back.app.exception.logic;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class InvalidNameFormatException extends ValidationException {

    public InvalidNameFormatException(String fieldName, String errorDescription) {
        super(
                BaseExceptionCode.INVALID_PART_OF_NAME_FORMAT,
                String.format("Field '%s': %s", fieldName, errorDescription)
        );
    }
}