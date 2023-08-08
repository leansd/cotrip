package cn.leancoding.cotrip.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object o);
    @Override
    public abstract int hashCode();
}
