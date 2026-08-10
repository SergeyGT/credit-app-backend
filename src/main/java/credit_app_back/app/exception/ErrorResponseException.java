package credit_app_back.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

@Getter
public class ErrorResponseException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;
    private final ProblemDetail problemDetail;

    public ErrorResponseException(HttpStatusCode httpStatusCode, ProblemDetail problemDetail, Throwable cause) {
        super(cause);
        this.httpStatusCode = httpStatusCode;
        this.problemDetail = problemDetail;
    }

    public ErrorResponseException(HttpStatusCode httpStatusCode, ProblemDetail problemDetail) {
        this.httpStatusCode = httpStatusCode;
        this.problemDetail = problemDetail;
    }
}