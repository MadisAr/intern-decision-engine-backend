package ee.taltech.inbankbackend.endpoint;

import ee.taltech.inbankbackend.data.DecisionResponseDTO;
import ee.taltech.inbankbackend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * RestControllerAdvice for handling errors
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidPersonalCodeException.class, InvalidLoanAmountException.class, InvalidLoanPeriodException.class, InvalidAgeException.class})
    public ResponseEntity<DecisionResponseDTO> handleBadRequestExceptions(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DecisionResponseDTO(null, null, e.getMessage()));
    }

    @ExceptionHandler(NoValidLoanException.class)
    public ResponseEntity<DecisionResponseDTO> handleNoValidLoanException(NoValidLoanException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DecisionResponseDTO(null, null, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DecisionResponseDTO> handleGeneralException(Exception e) {
        System.out.println(e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DecisionResponseDTO(null, null, "An unexpected error occurred"));
    }
}
