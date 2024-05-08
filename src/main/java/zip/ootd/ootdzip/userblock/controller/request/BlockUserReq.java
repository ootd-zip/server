package zip.ootd.ootdzip.userblock.controller.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.userblock.service.request.BlockUserSvcReq;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserReq {

    @Positive(message = "유저 ID는 양수여야 합니다.")
    private Long userId;

    public BlockUserSvcReq toServiceReq() {
        return BlockUserSvcReq.createBy(userId);
    }
}
