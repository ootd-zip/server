package zip.ootd.ootdzip.userblock.service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserBlockUnBlockSvcReq {

    private Long id;

    public static UserBlockUnBlockSvcReq createBy(Long id) {
        return UserBlockUnBlockSvcReq.builder()
                .id(id)
                .build();
    }
}
