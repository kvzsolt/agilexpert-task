package hu.agileexpert.smartos.exception.account;

public class IdMismatchException extends RuntimeException {

    public IdMismatchException(String resourceName, Long pathId, Long payloadId) {
        super(resourceName + " id mismatch: path id=" + pathId + ", payload id=" + payloadId);
    }
}

