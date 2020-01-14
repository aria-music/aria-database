package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.AriaException;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class Error {
    private String message;

    public Error(AriaException e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
