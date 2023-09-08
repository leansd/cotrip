package cn.leansd.base.livecheck;
import cn.leansd.base.model.DomainEntity;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "fake_user")
@Data
@NoArgsConstructor
@Builder
public class FakeUser extends DomainEntity {
    private String name;
    public FakeUser(String id) {
        super(id);
    }
}