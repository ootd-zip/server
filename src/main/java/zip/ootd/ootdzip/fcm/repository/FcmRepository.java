package zip.ootd.ootdzip.fcm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.fcm.domain.FcmInfo;

@Repository
public interface FcmRepository extends JpaRepository<FcmInfo, Long> {

    Optional<FcmInfo> findByFcmToken(String fcmToken);
}
