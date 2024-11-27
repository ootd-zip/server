package zip.ootd.ootdzip.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import zip.ootd.ootdzip.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByName(String name);

    @Query("SELECT u FROM User u JOIN FETCH u.fcmInfos WHERE u = :user")
    Optional<User> findWithFcmInfosByUser(@Param("user") User user);
}
