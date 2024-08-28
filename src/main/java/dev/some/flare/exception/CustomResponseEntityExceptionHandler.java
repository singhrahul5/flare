package dev.some.flare.exception;

import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        String errorMessage = ex.getFieldErrors().stream().findFirst().map(FieldError::getDefaultMessage).orElse("Bad" +
                " Request");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);

        Map<String, String> detailErrorMessage =
                ex.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                        (fe) -> Objects.requireNonNullElse(fe.getDefaultMessage(), "")));
        problemDetail.setProperty("detailErrorMessage", detailErrorMessage);
        return new ResponseEntity<>(problemDetail, headers, status);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        return new ResponseEntity<>(problemDetail, status);
    }

    @ExceptionHandler({
            NotAvailableException.class,
            EmailServiceException.class,
            NotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleErrorResponseExceptions(Exception ex) {
        ErrorResponse errorResponse = (ErrorResponse) ex;
        return new ResponseEntity<>(errorResponse.getBody(), errorResponse.getHeaders(), errorResponse.getStatusCode());
    }
}
