package credit_app_back.app.exception.factories;

import credit_app_back.app.exception.BaseException;
import credit_app_back.app.exception.GroupValidationException;
import credit_app_back.app.exception.JakartaValidationException;
import credit_app_back.app.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProblemDetailFactoryImpl implements ProblemDetailFactory {

    public ProblemDetail createBaseExceptionProblemDetail(BaseException e) {
        ProblemDetail problemDetail = createEmptyProblemDetail(e.getHttpStatus());
        addBaseExceptionToProblemDetail(problemDetail, e);
        return problemDetail;
    }

    private ProblemDetail createEmptyProblemDetail(HttpStatusCode httpStatusCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatusCode);
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    private void addBaseExceptionToProblemDetail(ProblemDetail pd, BaseException e) {
        pd.setDetail(e.getUserMessage());
        pd.setProperty("info", toProperties(e));
    }

    private Map<String, Object> toProperties(BaseException e) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("exceptionCode", e.getExceptionCode().name());
        properties.put("internalCode", e.getExceptionCode().getCode());
        properties.put("message", e.getUserMessage());
        return properties;
    }

    public ProblemDetail createInternalExceptionProblemDetail(Exception e) {
        ProblemDetail problemDetail = createEmptyProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("Internal server error: " + e.getMessage());
        return problemDetail;
    }

    public ProblemDetail createGroupValidationExceptionProblemDetail(GroupValidationException e) {
        ProblemDetail problemDetail = createEmptyProblemDetail(e.getHttpStatus());
        addGroupValidationExceptionToProblemDetail(problemDetail, e);
        return problemDetail;
    }

    private void addGroupValidationExceptionToProblemDetail(ProblemDetail pd, GroupValidationException e) {
        pd.setDetail(e.getUserMessage());
        pd.setProperty("info", toPropertiesList(e));
    }

    private List<Map<String, Object>> toPropertiesList(GroupValidationException e) {
        List<Map<String, Object>> propertiesList = new LinkedList<>();
        for (ValidationException ve : e.getExceptions()) {
            propertiesList.add(toProperties(ve));
        }
        return propertiesList;
    }

    public ProblemDetail createGroupValidationExceptionProblemDetail(MethodArgumentNotValidException e) {
        List<ValidationException> fieldExceptions = e.getBindingResult().getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> {
                    FieldError fieldError = (FieldError) error;
                    return new JakartaValidationException(fieldError);
                })
                .collect(Collectors.toList());

        GroupValidationException groupException = new GroupValidationException(fieldExceptions);
        return createGroupValidationExceptionProblemDetail(groupException);
    }
}