package cn.leancoding.cotrip.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
public abstract class OrmValueObject extends ValueObject{
    @Id
    private String id;
    protected OrmValueObject() {
        this.id = UUID.randomUUID().toString();
    }

}
