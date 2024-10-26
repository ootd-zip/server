package zip.ootd.ootdzip.brandrequest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.model.BrandRequestSearchRepoParam;

public interface BrandRequestRepositoryCustom {
    Page<BrandRequest> searchBrandRequests(BrandRequestSearchRepoParam param, Pageable pageable);
}
