package com.darvybm.project.apidevelopment.exception;



import com.darvybm.project.apidevelopment.utils.response.ApiResponse;
import com.darvybm.project.apidevelopment.utils.response.CustResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class RestExceptionHandler {
    private CustResponseBuilder custResponseBuilder;

    public RestExceptionHandler(CustResponseBuilder custResponseBuilder) {
        this.custResponseBuilder = custResponseBuilder;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> resolveException(UnauthorizedException exception) {
        System.out.println("Paso por UnauthorizedException: ");

        return exception.getApiResponse();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ResponseEntity<?> resolveException(ForbiddenException exception) {
        System.out.println("Paso por ForbiddenException: ");

        return exception.getApiResponse();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(BadRequestException exception) {
        System.out.println("Paso por BadRequestException: ");
        return exception.getApiResponse();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<?> resolveException(HttpRequestMethodNotSupportedException exception) {
        System.out.println("Paso por MethodNotAllowedException: ");

        return custResponseBuilder.buildResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method not allowed", exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<?> resolveException(ResourceNotFoundException exception) {
        System.out.println("Paso por ResourceNotFoundException: resolveException");

        return exception.getApiResponse();
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(MethodArgumentNotValidException ex) {
        System.out.println("Paso por MethodArgumentNotValidException: resolveException");
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        StringBuilder messages = new StringBuilder();

        for (FieldError error : fieldErrors) {
            messages.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(". ");
        }
        return custResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST.value(), messages.toString(), ex.getMessage() );
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(MethodArgumentTypeMismatchException ex) {
        System.out.println("Paso por MethodArgumentTypeMismatchException: resolveException");

        String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '"
                + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";
        return custResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST.value(), message, ex.getMessage());
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(HttpMessageNotReadableException ex) {
        System.out.println("Paso por HttpMessageNotReadableException: resolveException");

        String message = "Please provide Request Body in valid JSON format";
        return custResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST.value(), message, ex.getMessage());
    }
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(MissingServletRequestParameterException ex) {
        System.out.println("Paso por MissingServletRequestParameterException: resolveException");

        String message = "Please provide all The Parameters in a Valid Format";
        return custResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST.value(), message, ex.getMessage());
    }

    @ExceptionHandler({ AppException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> resolveException(AppException ex) {
        System.out.println("Paso por AppException: resolveException");

        return custResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex.getCause().getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> resolveException(UsernameNotFoundException ex) {
        System.out.println("Paso por UsernameNotFoundException: resolveException");

        return custResponseBuilder.buildResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex.getCause().getMessage());
    }

    @ExceptionHandler({ UserErrorException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> resolveException(UserErrorException exception) {
        System.out.println("Paso por UserErrorException: ");

        return exception.getApiResponse();
    }

}
