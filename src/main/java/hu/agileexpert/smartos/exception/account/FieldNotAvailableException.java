package hu.agileexpert.smartos.exception.account;

public class FieldNotAvailableException extends RuntimeException{
    public FieldNotAvailableException(String message){
        super(message);
    }
}