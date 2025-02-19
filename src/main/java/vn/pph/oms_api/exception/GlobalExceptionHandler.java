package vn.pph.oms_api.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Resource Not Found",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Resource Not Found",
                                    summary = "Handle resource not found exception",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 404,
                                                 "path": "/api/v1/...",
                                                 "error": "Not Found",
                                                 "message": "Resource not found"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleAppException(AppException exception, WebRequest request) {
        return buildErrorResponse(exception.getMessage(), NOT_FOUND, request);
    }

    @ExceptionHandler(RuntimeException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Invalid Request",
                                    summary = "Handle generic runtime exception",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 400,
                                                 "path": "/api/v1/...",
                                                 "error": "Bad Request",
                                                 "message": "Unexpected error occurred"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleRuntimeException(RuntimeException exception, WebRequest request) {
        return buildErrorResponse(exception.getMessage(), BAD_REQUEST, request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    summary = "Handle invalid request body or missing parameters",
                                    value = """
                                        {
                                             "timestamp": "2024-04-07T11:38:56.368+00:00",
                                             "status": 400,
                                             "path": "/api/v1/...",
                                             "error": "Invalid Request",
                                             "message": "Required parameter {param} is missing or invalid"
                                        }
                                        """
                            ))})
    })
    public ErrorResponse handleValidationExceptions(Exception exception, WebRequest request) {
        String errorMessage;

        if (exception instanceof MethodArgumentNotValidException ex && ex.getFieldError() != null) {
            errorMessage = ex.getFieldError().getDefaultMessage();
        } else if (exception instanceof MissingServletRequestParameterException ex) {
            errorMessage = "Required parameter '" + ex.getParameterName() + "' is missing";
        } else {
            errorMessage = "Invalid request";
        }

        return buildErrorResponse(errorMessage, BAD_REQUEST, request);
    }


    private ErrorResponse buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setError(status.getReasonPhrase());
        response.setStatus(status.value());
        response.setMessage(message);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
