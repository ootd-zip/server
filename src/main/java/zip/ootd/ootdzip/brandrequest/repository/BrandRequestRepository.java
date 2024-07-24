package zip.ootd.ootdzip.brandrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;

@Repository
public interface BrandRequestRepository extends JpaRepository<BrandRequest, Long> {

}
