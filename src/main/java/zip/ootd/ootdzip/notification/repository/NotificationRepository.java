package zip.ootd.ootdzip.notification.repository;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.notification.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n from Notification n where n.receiver.id = :userId and n.isRead = :isRead AND n.sender.id NOT IN :userIds ")
    Slice<Notification> findByUserIdAndIsReadAndSenderIdNotIn(@Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);

    @Query("SELECT count(n) from Notification n where n.receiver.id = :userId and n.isRead = :isRead")
    Long countByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
}
