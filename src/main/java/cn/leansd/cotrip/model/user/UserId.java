package cn.leansd.cotrip.model.user;
import cn.leansd.base.model.GenericId;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class UserId extends GenericId {
    public UserId(String value) {
        super(value);
    }
}