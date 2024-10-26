package zip.ootd.ootdzip.brandrequest.service.request;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class BrandRequestSearchSvcReq {

    private String searchText;
    private BrandRequestStatus searchStatus;
    private Pageable pageable;
}
