package cn.leansd.base.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
public abstract class PersistentableValueObject extends ValueObject{
    protected PersistentableValueObject() {
        this.id = UUID.randomUUID().toString();
    }

}
