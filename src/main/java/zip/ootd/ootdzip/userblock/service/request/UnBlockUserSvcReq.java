package zip.ootd.ootdzip.userblock.service.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UnBlockUserSvcReq {

    private Long id;

    public static UnBlockUserSvcReq createBy(Long id) {
        return UnBlockUserSvcReq.builder()
                .id(id)
                .build();
    }
}
