package zip.ootd.ootdzip.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.report.domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
