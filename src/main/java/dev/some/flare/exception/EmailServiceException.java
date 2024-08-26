package dev.some.flare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

public class EmailServiceException extends RuntimeException implements ErrorResponse {

    private final HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(statusCode, "Email sending failed due to a server error. Please try " +
                "again later.");
    }
}
