package zip.ootd.ootdzip.brandrequest.service.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class BrandRequestApproveSvcReq {

    private List<Long> brandRequestId;
    private String brandName;
    private String brandEngName;
}
