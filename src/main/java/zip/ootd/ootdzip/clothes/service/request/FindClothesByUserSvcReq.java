package zip.ootd.ootdzip.clothes.service.request;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Getter
public class FindClothesByUserSvcReq extends CommonPageRequest {

    private final Long userId;

    private final Pageable pageable;

    @Builder
    public FindClothesByUserSvcReq(Long userId, Pageable pageable) {
        this.userId = userId;
        this.pageable = pageable;
    }
}
