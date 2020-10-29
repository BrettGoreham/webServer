package webServer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Unable to find requested information")
public class NoRecordsFoundException extends RuntimeException{
}
