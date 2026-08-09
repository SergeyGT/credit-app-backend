package credit_app_back.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final BaseExceptionCode exceptionCode;
    private final String userMessage;
    private final HttpStatus httpStatus;

    public BaseException(BaseExceptionCode exceptionCode) {
        super(exceptionCode.getDefaultMessage());
        this.exceptionCode = exceptionCode;
        this.userMessage = exceptionCode.getDefaultMessage();
        this.httpStatus = exceptionCode.getHttpStatus();
    }

    public BaseException(BaseExceptionCode exceptionCode, String userMessage) {
        super(userMessage);
        this.exceptionCode = exceptionCode;
        this.userMessage = userMessage;
        this.httpStatus = exceptionCode.getHttpStatus();
    }

    public BaseException(BaseExceptionCode exceptionCode, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.exceptionCode = exceptionCode;
        this.userMessage = userMessage;
        this.httpStatus = exceptionCode.getHttpStatus();
    }
}