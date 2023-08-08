package cn.leancoding.cotrip.model.user;
import cn.leancoding.cotrip.base.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
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