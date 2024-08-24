package dev.some.flare.exception;

import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setProperty("moreDetails", ex.getDetailMessageArguments());
        return new ResponseEntity<>(problemDetail, headers, status);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        return new ResponseEntity<>(problemDetail, status);
    }

    @ExceptionHandler({NotAvailableException.class})
    public ResponseEntity<ProblemDetail> handleNotAvailableException(Exception ex) {
        ErrorResponse errorResponse = (ErrorResponse) ex;
        return new ResponseEntity<>(errorResponse.getBody(), errorResponse.getHeaders(), errorResponse.getStatusCode());
    }
}
