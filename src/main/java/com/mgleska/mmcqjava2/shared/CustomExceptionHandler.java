package com.mgleska.mmcqjava2.shared;

import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.AuthenticationException;
import picocli.CommandLine;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    public record FieldError(
        @NotBlank String field,
        @NotBlank String message
    ) { }

    public record ErrorResponse(
        @NotNull  int status,
        @NotBlank String error,
        @NotBlank String message,
                  List<FieldError> errors,
        @NotBlank String path
    ) {
        @SuppressWarnings("unused")
        @NotBlank
        public String getTimestamp() {
            return Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
        }
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleInvalidArgument(MethodArgumentNotValidException ex,  HttpServletRequest request)
    {
        List<FieldError> errorList = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(
            error -> errorList.add(new FieldError(error.getField(), error.getDefaultMessage()))
        );
        String message = "Validation failed for object '" + ex.getObjectName() + "'. Number of errors: " + ex.getErrorCount();

        return new ErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT.value(), HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(), message, errorList, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleInvalidArgument(HttpMessageNotReadableException ex,  HttpServletRequest request)
    {
        String message = "Unable to parse request body";
        List<FieldError> errorList = new ArrayList<>();

        Throwable cause = ex.getCause();

        if (cause instanceof JsonParseException parseEx) {
            message = "Invalid JSON format: " + parseEx.getCause().getMessage();
        }

        if (cause instanceof InvalidFormatException mismatchEx) {
            errorList.add(new FieldError(mismatchEx.getPath().getLast().getPropertyName(),  mismatchEx.getOriginalMessage()));
        }

        if (cause instanceof MismatchedInputException mismatchEx) {
            errorList.add(new FieldError(mismatchEx.getPath().getLast().getPropertyName(), mismatchEx.getOriginalMessage()));
        }

        return new ErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT.value(), HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(), message, errorList, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(AppValidationException.class)
    public ErrorResponse handleInvalidArgument(AppValidationException ex,  HttpServletRequest request)
    {
        String message = ex.getField() + ": " + ex.getMessage();
        List<FieldError> errorList = new ArrayList<>();
        errorList.add(new FieldError(ex.getField(), ex.getMessage()));

        return new ErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT.value(), HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(), message, errorList, request.getRequestURI());
    }

    // handle Filter exception
    public void handleFilterException(HttpServletRequest req, HttpServletResponse res, RuntimeException ex) throws IOException {
        ErrorResponse error;
        if (ex instanceof AppAuthException || ex instanceof AppNeverException) {
            error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), null, req.getRequestURI());
        }
        else if (ex instanceof AuthenticationException ) {
            error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "", null, req.getRequestURI());
        }
        else {
            throw ex;
        }

        var mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write(mapper.writeValueAsString(error));
    }

    // handle CLI exception
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        CommandLine.ParseResult parseResult) {

        String message;
        if (ex instanceof AppValidationException validationEx) {
            message = "[ERROR] " + validationEx.getField() + ": " + validationEx.getMessage();
        }
        else {
            message = "[ERROR] " + ex.getMessage();
        }
        if (System.console() != null) {
            message = "\u001B[31m" + message + "\u001B[0m";
        }
        System.out.println(message);

        return cmd.getExitCodeExceptionMapper() != null
            ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
            : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
