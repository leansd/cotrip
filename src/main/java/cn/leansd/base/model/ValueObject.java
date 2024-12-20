package cn.leansd.base.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
public abstract class ValueObject {
    @Id
    protected String id;
    @Override
    public abstract boolean equals(Object o);
    @Override
    public abstract int hashCode();

    protected ValueObject() {
        this.id = UUID.randomUUID().toString();
    }

    protected ValueObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
