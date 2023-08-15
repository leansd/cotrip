package cn.leancoding.cotrip.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
public abstract class PersistentableValueObject extends ValueObject{
    @Id
    private String id;
    protected PersistentableValueObject() {
        this.id = UUID.randomUUID().toString();
    }

}
