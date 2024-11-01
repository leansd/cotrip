package cn.leansd.base.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class DomainEntity {
    @Id
    private String id;
    private String creatorId;
    private long createdTime;
    protected DomainEntity() {
        this.id = UUID.randomUUID().toString();
    }

    protected DomainEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntity that = (DomainEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    protected void addCreationInfo(UserId createdBy) {
        this.creatorId = createdBy.getId();
        this.createdTime = System.currentTimeMillis();
    }
}
