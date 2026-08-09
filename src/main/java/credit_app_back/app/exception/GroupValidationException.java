package credit_app_back.app.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class GroupValidationException extends ValidationException {

    private final List<ValidationException> exceptions;

    public GroupValidationException(List<? extends ValidationException> exceptions) {
        super(BaseExceptionCode.GROUP_VALIDATION_EXCEPTION);
        this.exceptions = new ArrayList<>(exceptions);
    }

    public GroupValidationException(ValidationException singleException) {
        this(Collections.singletonList(singleException));
    }

    public List<ValidationException> getExceptions() {
        return Collections.unmodifiableList(exceptions);
    }
}