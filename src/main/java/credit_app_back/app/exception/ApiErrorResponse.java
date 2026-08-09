package credit_app_back.app.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private final int code;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
    private final Map<String, String> validationErrors;
}