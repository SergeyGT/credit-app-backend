package credit_app_back.app.exception.logic;


import credit_app_back.app.exception.BaseException;
import credit_app_back.app.exception.BaseExceptionCode;

public class ApplicationNotFoundException extends BaseException {
    public ApplicationNotFoundException(Long id) {
        super(BaseExceptionCode.APPLICATION_NOT_FOUND, "Application not found with id: " + id);
    }
}