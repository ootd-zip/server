package zip.ootd.ootdzip.brandrequest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BrandRequest extends BaseEntity {

    private String requestContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User requestUser;

    @Enumerated(EnumType.STRING)
    private BrandRequestStatus requestStatus;

    private String reason;

    public static BrandRequest createBy(String requestContents, User requestUser) {
        return BrandRequest.builder()
                .requestContents(requestContents)
                .requestUser(requestUser)
                .requestStatus(BrandRequestStatus.REQUEST)
                .build();
    }

    public void approveBrandRequest() {
        requestStatus = BrandRequestStatus.APPROVED;
    }

}
