package zip.ootd.ootdzip.brandrequest.controller.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class BrandRequestSearchRes {
    private Long id;
    private String requestUserName;
    private String requestContents;
    private BrandRequestStatus brandRequestStatus;
    private LocalDateTime createdAt;

    public static BrandRequestSearchRes of(BrandRequest brandRequest) {
        return BrandRequestSearchRes.builder()
                .id(brandRequest.getId())
                .requestUserName(brandRequest.getRequestUser().getName())
                .requestContents(brandRequest.getRequestContents())
                .brandRequestStatus(brandRequest.getRequestStatus())
                .createdAt(brandRequest.getCreatedAt())
                .build();
    }
}
