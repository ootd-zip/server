package zip.ootd.ootdzip.brandrequest.repository.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class BrandRequestSearchRepoParam {
    private BrandRequestStatus brandRequestStatus;
    private String searchText;
}
