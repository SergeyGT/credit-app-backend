package credit_app_back.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseExceptionCode {

    GROUP_VALIDATION_EXCEPTION(400, HttpStatus.BAD_REQUEST, "Group validation failed"),
    DTO_FIELD_VALIDATION_ERROR(401, HttpStatus.BAD_REQUEST, "Invalid field value"),

    MISMATCH_CLIENT_DATA(402, HttpStatus.BAD_REQUEST, "Client data mismatch"),
    MISSING_PART_OF_EMPLOYMENT_DATA(408, HttpStatus.BAD_REQUEST, "Incomplete employment data"),
    INVALID_PHONE_FORMAT(409, HttpStatus.BAD_REQUEST, "Invalid phone format"),
    INVALID_PASSPORT_FORMAT(410, HttpStatus.BAD_REQUEST, "Invalid passport format"),
    INVALID_PART_OF_NAME_FORMAT(411, HttpStatus.BAD_REQUEST, "Invalid name format"),

    APPLICATION_NOT_FOUND(403, HttpStatus.NOT_FOUND, "Credit application not found"),
    APPLICATION_ALREADY_PROCESSED(404, HttpStatus.CONFLICT, "Application already processed"),
    WRONG_APPLICATION_STATUS_FOR_SIGNING(406, HttpStatus.BAD_REQUEST, "Cannot sign this application"),

    AGREEMENT_NOT_FOUND(405, HttpStatus.NOT_FOUND, "Agreement not found"),
    AGREEMENT_ALREADY_SIGNED(407, HttpStatus.CONFLICT, "Agreement already signed"),

    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final int code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;
}