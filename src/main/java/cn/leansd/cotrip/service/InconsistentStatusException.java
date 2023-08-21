package cn.leansd.cotrip.service;

import cn.leansd.base.exception.ApplicationException;

public class InconsistentStatusException extends ApplicationException {
    public InconsistentStatusException(String message) {
        super(message);
    }
}
