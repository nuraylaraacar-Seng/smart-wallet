package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.domain.exception.CurrencyMismatchException;
import com.smartwallet.domain.exception.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ProblemDetail handleInsufficientBalance(InsufficientBalanceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Insufficient Balance");
        problem.setType(URI.create("https://smartwallet.com/errors/insufficient-balance"));
        problem.setProperty("walletId", ex.getWalletId());
        problem.setProperty("requestedAmount", ex.getRequestedAmount());
        problem.setProperty("availableAmount", ex.getAvailableAmount());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ProblemDetail handleCurrencyMismatch(CurrencyMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Currency Mismatch");
        problem.setType(URI.create("https://smartwallet.com/errors/currency-mismatch"));
        problem.setProperty("expectedCurrency", ex.getExpectedCurrency().getCurrencyCode());
        problem.setProperty("actualCurrency", ex.getActualCurrency().getCurrencyCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Argument");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}