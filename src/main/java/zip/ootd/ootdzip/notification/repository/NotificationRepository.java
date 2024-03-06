package zip.ootd.ootdzip.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.notification.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
