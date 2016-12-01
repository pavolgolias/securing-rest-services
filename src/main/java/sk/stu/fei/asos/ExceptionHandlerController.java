package sk.stu.fei.asos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@ControllerAdvice
public class ExceptionHandlerController {
    private final HttpServletRequest httpServletRequest;
    private final ObjectMapper jsonMapper;

    @Autowired
    public ExceptionHandlerController(HttpServletRequest httpServletRequest, ObjectMapper jsonMapper) {
        this.httpServletRequest = httpServletRequest;
        this.jsonMapper = jsonMapper;
    }

    @ExceptionHandler({Throwable.class, Exception.class})
    public void serverError(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    private void handleException(Exception ex, HttpStatus status, HttpServletResponse resp) throws IOException {
        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final String exceptionMessage = ex.getMessage().isEmpty() ? status.getReasonPhrase().trim() : ex.getMessage().trim();
        final ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setError(exceptionMessage);
        errorMessage.setException(ex.getClass().getCanonicalName());
        errorMessage.setMessage(exceptionMessage);
        errorMessage.setStatus(status.value());
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setPath(httpServletRequest.getServletPath());
        resp.getOutputStream().print(jsonMapper.writeValueAsString(errorMessage));
    }

    private static class ErrorMessage {
        private long timestamp;
        private int status;
        private String error;
        private String exception;
        private String message;
        private String path;

        private long getTimestamp() {
            return timestamp;
        }

        private void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        private int getStatus() {
            return status;
        }

        private void setStatus(int status) {
            this.status = status;
        }

        private String getError() {
            return error;
        }

        private void setError(String error) {
            this.error = error;
        }

        private String getException() {
            return exception;
        }

        private void setException(String exception) {
            this.exception = exception;
        }

        private String getMessage() {
            return message;
        }

        private void setMessage(String message) {
            this.message = message;
        }

        private String getPath() {
            return path;
        }

        private void setPath(String path) {
            this.path = path;
        }
    }
}
