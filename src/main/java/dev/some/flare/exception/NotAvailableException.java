package dev.some.flare.exception;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

public class NotAvailableException extends RuntimeException implements ErrorResponse {

    private final HttpStatusCode statusCode = HttpStatus.CONFLICT;

    public NotAvailableException(String message) {
        super(message);
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(statusCode, getMessage());
    }
}
