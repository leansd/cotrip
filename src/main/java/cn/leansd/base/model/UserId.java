package cn.leansd.base.model;
import cn.leansd.base.model.GenericId;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class UserId extends GenericId {
    public UserId(String value) {
        super(value);
    }

    public static UserId of(String userId) {
        return new UserId(userId);
    }
}