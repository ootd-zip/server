package zip.ootd.ootdzip.fcm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.fcm.domain.FcmInfo;
import zip.ootd.ootdzip.user.domain.User;

import java.util.Optional;

@Repository
public interface FcmRepository extends JpaRepository<FcmInfo, Long> {

    boolean existsByFcmToken(String fcmToken);

    Optional<FcmInfo> findByFcmToken(String fcmToken);
}
