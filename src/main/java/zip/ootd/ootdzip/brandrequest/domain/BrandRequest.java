package zip.ootd.ootdzip.brandrequest.domain;

import jakarta.persistence.*;
import lombok.*;
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
}
