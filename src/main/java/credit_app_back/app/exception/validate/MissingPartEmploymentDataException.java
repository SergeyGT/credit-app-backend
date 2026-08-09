package credit_app_back.app.exception.validate;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class MissingPartEmploymentDataException extends ValidationException {

    public MissingPartEmploymentDataException(String missingField) {
        super(
                BaseExceptionCode.MISSING_PART_OF_EMPLOYMENT_DATA,
                "Missing employment data: " + missingField
        );
    }
}