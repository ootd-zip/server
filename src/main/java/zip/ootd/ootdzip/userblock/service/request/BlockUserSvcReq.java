package zip.ootd.ootdzip.userblock.service.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BlockUserSvcReq {
    private Long userId;

    public static BlockUserSvcReq createBy(Long userId) {
        return BlockUserSvcReq.builder()
                .userId(userId)
                .build();
    }
}
