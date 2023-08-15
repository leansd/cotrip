package cn.leancoding.cotrip.model.user;
import cn.leancoding.cotrip.base.model.DomainEntity;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Data
@NoArgsConstructor
@Builder
public class User extends DomainEntity {
    private String name;
    public User(String id) {
        super(id);
    }
}