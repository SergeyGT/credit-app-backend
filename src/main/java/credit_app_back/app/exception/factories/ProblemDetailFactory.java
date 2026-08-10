package credit_app_back.app.exception.factories;

import credit_app_back.app.exception.BaseException;
import credit_app_back.app.exception.GroupValidationException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ProblemDetailFactory {

    ProblemDetail createBaseExceptionProblemDetail(BaseException e);

    ProblemDetail createInternalExceptionProblemDetail(Exception e);

    ProblemDetail createGroupValidationExceptionProblemDetail(GroupValidationException e);

    ProblemDetail createGroupValidationExceptionProblemDetail(MethodArgumentNotValidException e);
}