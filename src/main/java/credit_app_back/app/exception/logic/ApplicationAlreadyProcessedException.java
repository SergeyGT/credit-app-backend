package credit_app_back.app.exception.logic;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class ApplicationAlreadyProcessedException extends ValidationException {

    public ApplicationAlreadyProcessedException(Long id) {
        super(
                BaseExceptionCode.APPLICATION_ALREADY_PROCESSED,
                "Application with id " + id + " has already been processed."
        );
    }

    public ApplicationAlreadyProcessedException(Long id, String message) {
        super(
                BaseExceptionCode.APPLICATION_ALREADY_PROCESSED,
                message
        );
    }
}