package cn.leancoding.cotrip.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {
    private String userId;
    private String phoneNumber;
    private String name;
    private String userName;
    private String email;
}
