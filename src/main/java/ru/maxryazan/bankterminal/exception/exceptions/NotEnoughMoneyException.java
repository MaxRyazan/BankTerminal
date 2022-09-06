package ru.maxryazan.bankterminal.exception.exceptions;

public class NotEnoughMoneyException extends  RuntimeException{

    public NotEnoughMoneyException(String message){
        super(message);
    }
}
