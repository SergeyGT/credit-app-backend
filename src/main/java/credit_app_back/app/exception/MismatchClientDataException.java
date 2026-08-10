package credit_app_back.app.exception;

import credit_app_back.app.exception.BaseExceptionCode;
import credit_app_back.app.exception.ValidationException;

public class MismatchClientDataException extends ValidationException {

    public MismatchClientDataException() {
        super(
                BaseExceptionCode.MISMATCH_CLIENT_DATA,
                "Client with this passport has different data from existed client."
        );
    }

    public MismatchClientDataException(String message) {
        super(
                BaseExceptionCode.MISMATCH_CLIENT_DATA,
                message
        );
    }
}
