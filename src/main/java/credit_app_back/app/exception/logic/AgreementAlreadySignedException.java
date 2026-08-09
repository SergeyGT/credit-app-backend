package credit_app_back.app.exception.logic;

import credit_app_back.app.exception.BaseException;
import credit_app_back.app.exception.BaseExceptionCode;

public class AgreementAlreadySignedException extends BaseException {
    public AgreementAlreadySignedException(Long id) {
        super(BaseExceptionCode.AGREEMENT_ALREADY_SIGNED, "Agreement already signed for application: " + id);
    }
}