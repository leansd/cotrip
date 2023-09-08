package cn.leansd.base.livecheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LiveCheckController {
    private final FakeUserRepository userRepository;
    public LiveCheckController(FakeUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/live-check")
    public List<FakeUser> getAllUsers() {
        return (List<FakeUser>) userRepository.findAll();
    }
}