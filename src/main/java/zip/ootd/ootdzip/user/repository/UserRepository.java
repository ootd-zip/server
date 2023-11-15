package zip.ootd.ootdzip.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);
}
