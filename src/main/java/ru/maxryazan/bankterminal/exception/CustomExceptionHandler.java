package ru.maxryazan.bankterminal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.maxryazan.bankterminal.exception.exceptions.CreditNotFoundException;
import ru.maxryazan.bankterminal.exception.exceptions.InvalidDataException;
import ru.maxryazan.bankterminal.exception.exceptions.NotEnoughMoneyException;
import ru.maxryazan.bankterminal.exception.exceptions.UserNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CreditNotFoundException.class)
    public ResponseEntity<?> handleCreditNotFoundException(){
        Map<String, Object> body = new HashMap<>();
        body.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")), "Credit not found");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(){
        Map<String, Object> body = new HashMap<>();
        body.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")),
                "Data not valid (check phone numbers / sum / balance / credit status)");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<?> handleNotEnoughMoneyException(){
        Map<String, Object> body = new HashMap<>();
        body.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")), "Not enough money");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(){
        Map<String, Object> body = new HashMap<>();
        body.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")), "Client not found");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}
