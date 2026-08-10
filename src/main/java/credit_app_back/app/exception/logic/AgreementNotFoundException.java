package credit_app_back.app.exception.logic;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class AgreementNotFoundException extends ValidationException {

    public AgreementNotFoundException(Long applicationId) {
        super(
                BaseExceptionCode.AGREEMENT_NOT_FOUND,
                "Agreement for application with id " + applicationId + " was not found."
        );
    }
}