package com.friends.manage.exception.handler;

import com.friends.manage.exception.PersonNotFoundException;
import com.friends.manage.exception.RenameIsNotPermittedException;
import com.friends.manage.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RenameIsNotPermittedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerRenameNoPermittedException(RenameIsNotPermittedException ex){
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerPersonNotFoundException(PersonNotFoundException ex){
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("서버오류 : {}", ex.getMessage(), ex);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류가 발생하였습니다");
    }


}
