package zip.ootd.ootdzip.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.notification.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n from Notification n where n.receiver.id = :userId and n.isRead = :isRead")
    Slice<Notification> findByUserIdAndIsRead(@Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            Pageable pageable);

    @Query("SELECT count(n) from Notification n where n.receiver.id = :userId and n.isRead = :isRead")
    Long findCountByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
}
