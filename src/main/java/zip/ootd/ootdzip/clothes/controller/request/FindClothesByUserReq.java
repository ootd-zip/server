package zip.ootd.ootdzip.clothes.controller.request;

import jakarta.validation.constraints.Positive;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.service.request.FindClothesByUserSvcReq;

@Setter
public class FindClothesByUserReq {

    @Positive(message = "유저 ID는 양수여야 합니다.")
    private Long userId;

    public FindClothesByUserSvcReq toServiceRequest() {
        return FindClothesByUserSvcReq.builder()
                .userId(this.userId)
                .build();
    }
}
