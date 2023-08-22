package cn.leansd.base.controller;

import cn.leansd.base.exception.RequestedResourceNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RequestedResourceNotFound.class)
    public ResponseEntity<Object> handleRequestedResourceNotFoundException(RequestedResourceNotFound ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
