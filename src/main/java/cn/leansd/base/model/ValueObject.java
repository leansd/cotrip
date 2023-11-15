package cn.leansd.base.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ValueObject {
    @Id
    String id;
    @Override
    public abstract boolean equals(Object o);
    @Override
    public abstract int hashCode();
}
