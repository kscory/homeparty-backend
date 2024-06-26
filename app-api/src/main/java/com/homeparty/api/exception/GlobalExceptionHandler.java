package com.homeparty.api.exception;

import abstraction.exeption.HomePartyException;
import com.homeparty.api.dto.ApiResponse;
import com.homeparty.identity.domain.exception.IdentityException;
import com.homeparty.invitation.domain.exception.InvitationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<ApiResponse<Object>> handle(final AuthenticationException e) {
        log.error("[! AuthenticationException !]: {}", e.getMessage(), e);
        var exceptionCode = ApiExceptionCode.UNAUTHORIZED_ERROR;
        return ResponseEntity.status(exceptionCode.getStatus())
                .body(ApiResponse.failure(exceptionCode));
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<ApiResponse<Object>> handleForbidden(AccessDeniedException e) {
        log.error("[! AccessDeniedException !]: {}", e.getMessage(), e);
        var exceptionCode = ApiExceptionCode.FORBIDDEN_ERROR;
        return ResponseEntity.status(exceptionCode.getStatus())
                .body(ApiResponse.failure(exceptionCode));
    }

    @ExceptionHandler({
            ApiException.class,
            IdentityException.class,
            InvitationException.class
    })
    protected  ResponseEntity<ApiResponse<Object>> handleHomePartyException(final HomePartyException exception) {
        log.error("[! HomePartyException !]: {}", exception.getMessage(), exception);
        var exceptionCode = exception.getExceptionCode();
        return ResponseEntity.status(exceptionCode.getStatus())
                .body(ApiResponse.failure(exceptionCode));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception) {
        log.error("[! UnknownException !]: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(ApiExceptionCode.INTERNAL_SERVER_ERROR));
    }
}
