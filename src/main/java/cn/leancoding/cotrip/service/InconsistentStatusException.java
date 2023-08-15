package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.ApplicationException;

public class InconsistentStatusException extends ApplicationException {
    public InconsistentStatusException(String message) {
        super(message);
    }
}
