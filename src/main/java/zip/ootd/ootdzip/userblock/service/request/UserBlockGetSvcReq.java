package zip.ootd.ootdzip.userblock.service.request;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserBlockGetSvcReq {
    private Pageable pageable;

    public static UserBlockGetSvcReq of(CommonPageRequest request) {
        return UserBlockGetSvcReq.builder()
                .pageable(request.toPageable())
                .build();
    }
}
