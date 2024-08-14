package zip.ootd.ootdzip.brandrequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;

@Repository
public interface BrandRequestRepository extends JpaRepository<BrandRequest, Long> {

    Optional<BrandRequest> findOneByRequestContents(String requestContents);

    List<BrandRequest> findByIdIn(List<Long> ids);
}
