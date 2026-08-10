package credit_app_back.app.exception.logic;


import credit_app_back.app.entity.CreditApplicationStatus;
import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class ApplicationCanNotSignException extends ValidationException {

    public ApplicationCanNotSignException(Long applicationId, CreditApplicationStatus status) {
        super(
                BaseExceptionCode.WRONG_APPLICATION_STATUS_FOR_SIGNING,
                "Can not sign application with id " + applicationId + ". It has " + status + " status."
        );
    }
}
