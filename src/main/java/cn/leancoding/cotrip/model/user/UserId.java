package cn.leancoding.cotrip.model.user;
import cn.leancoding.cotrip.base.GenericId;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class UserId extends GenericId {
    public UserId(String value) {
        super(value);
    }
}