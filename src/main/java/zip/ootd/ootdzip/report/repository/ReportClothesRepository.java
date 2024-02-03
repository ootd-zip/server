package zip.ootd.ootdzip.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.report.domain.ReportClothes;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ReportClothesRepository extends JpaRepository<ReportClothes, Long> {

    boolean existsByClothesAndReporter(Clothes clothes, User reporter);
}
