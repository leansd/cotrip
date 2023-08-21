package cn.leansd.base.exception;

import cn.leansd.base.exception.ApplicationException;

public class InconsistentStatusException extends ApplicationException {
    public InconsistentStatusException(String message) {
        super(message);
    }
}
