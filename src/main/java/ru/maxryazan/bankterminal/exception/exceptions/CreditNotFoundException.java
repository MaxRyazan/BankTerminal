package ru.maxryazan.bankterminal.exception.exceptions;

public class CreditNotFoundException extends  RuntimeException{
    public CreditNotFoundException(String message){
        super(message);
    }
}
