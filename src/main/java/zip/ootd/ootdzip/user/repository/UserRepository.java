package zip.ootd.ootdzip.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);
}
