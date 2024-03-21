package zip.ootd.ootdzip.user.controller.request;

import org.springframework.data.domain.PageRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.user.service.request.UserSearchSvcReq;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserSearchReq {
    private String name;
    private Integer page = 0;
    private Integer pageSize = 30;

    public UserSearchSvcReq toServiceRequest() {
        return UserSearchSvcReq.builder()
                .name(name)
                .pageable(PageRequest.of(page, pageSize))
                .build();

    }
}
