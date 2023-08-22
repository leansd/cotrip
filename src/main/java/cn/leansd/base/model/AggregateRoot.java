package cn.leansd.base.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class AggregateRoot<A extends AggregateRoot<A>> extends AbstractAggregateRoot<A>  {
    @Id
    private String id;
    private String createdBy;
    private long createdAt;
    protected AggregateRoot() {
        this.id = UUID.randomUUID().toString();
    }

    public AggregateRoot(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot that = (AggregateRoot) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    protected void addCreationInfo(UserId createdBy) {
        this.createdBy = createdBy.getId();
        this.createdAt = System.currentTimeMillis();
    }

    public void registerDomainEvent(DomainEvent event){
        this.registerEvent(event);
    }
}
