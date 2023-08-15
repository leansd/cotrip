package cn.leancoding.cotrip.base;

public class ApplicationException extends Exception{
    String message;
    public ApplicationException(String message) {
        this.message = message;
    }
}
