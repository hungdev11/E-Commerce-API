package vn.pph.oms_api.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AppException.class})
    public ErrorResponse handleAppException(AppException exception, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setError(NOT_FOUND.getReasonPhrase());
        response.setStatus(NOT_FOUND.value());
        response.setMessage(exception.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    @ExceptionHandler({RuntimeException.class})
    public ErrorResponse handleAppException(RuntimeException exception, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setError(BAD_REQUEST.getReasonPhrase());
        response.setStatus(BAD_REQUEST.value());
        response.setMessage(exception.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
