package com.david.bookms.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorResponse> handleUserNotFoundException(ConstraintViolationException ex,
                                                                           WebRequest request) {
        List<String> details = new ArrayList<>();
        details.addAll(ex.getConstraintViolations().stream().map(v->v.getMessage()).collect(Collectors.toList()));
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
