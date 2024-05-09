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
public class UserBlockSvcReq {
    private Long userId;

    public static UserBlockSvcReq createBy(Long userId) {
        return UserBlockSvcReq.builder()
                .userId(userId)
                .build();
    }
}
