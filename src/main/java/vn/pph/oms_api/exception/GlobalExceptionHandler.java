package vn.pph.oms_api.exception;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /*
    1. Handle validation ({ConstraintViolationException.class, MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    2. Resource not found
    3. invalid data
    4. something else
    */
    @ExceptionHandler({AppException.class})
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when the data invalid. (@RequestBody, @RequestParam, @PathVariable)",
                                    summary = "Handle Bad Request",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 400,
                                                 "path": "/api/v1/...",
                                                 "error": "Invalid Payload",
                                                 "message": "{data} must be not blank"
                                             }
                                            """
                            ))})
    })
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
