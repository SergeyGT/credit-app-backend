package credit_app_back.app.exception;

public class MismatchClientDataException extends ValidationException {

    public MismatchClientDataException() {
        super(
                BaseExceptionCode.MISMATCH_CLIENT_DATA,
                "Client with this passport has different data from existed client."
        );
    }
}
