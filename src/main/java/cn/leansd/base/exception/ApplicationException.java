package cn.leansd.base.exception;

public class ApplicationException extends Exception{
    String message;
    public ApplicationException(String message) {
        this.message = message;
    }
}
