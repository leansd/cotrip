package cn.leansd.base.livecheck;

import org.springframework.data.repository.CrudRepository;
public interface FakeUserRepository extends CrudRepository<FakeUser, Long> {
}