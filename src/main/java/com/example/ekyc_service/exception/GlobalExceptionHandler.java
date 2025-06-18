package com.example.ekyc_service.exception;

import com.example.ekyc_service.constants.ErrorConstants;
import com.example.ekyc_service.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.example.ekyc_service.constants.ErrorConstants.INVALID_INPUT_ERROR_CODE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    final String errorTimestamp = LocalDateTime.now().toString();

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(INVALID_INPUT_ERROR_CODE);
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(errorTimestamp);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException e, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorConstants.INTERNAL_SERVER_ERROR);
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(errorTimestamp);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }
}
