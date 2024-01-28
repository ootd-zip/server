package zip.ootd.ootdzip.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.report.domain.ReportOotd;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ReportOotdRepository extends JpaRepository<ReportOotd, Long> {

    boolean existsByOotdAndUser(Ootd ootd, User user);

    Integer countByOotd(Ootd ootd);

}
