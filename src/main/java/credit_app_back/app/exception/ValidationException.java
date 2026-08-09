package credit_app_back.app.exception;

public abstract class ValidationException extends BaseException {
    
    public ValidationException(BaseExceptionCode code) {
        super(code);
    }

    public ValidationException(BaseExceptionCode code, String userMessage) {
        super(code, userMessage);
    }

    public ValidationException(BaseExceptionCode code, String userMessage, Throwable cause) {
        super(code, userMessage, cause);
    }
}