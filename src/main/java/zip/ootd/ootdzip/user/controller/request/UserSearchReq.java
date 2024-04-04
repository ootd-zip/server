package zip.ootd.ootdzip.user.controller.request;

import org.springframework.data.domain.PageRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.valid.EnumValid;
import zip.ootd.ootdzip.user.data.UserSearchType;
import zip.ootd.ootdzip.user.service.request.UserSearchSvcReq;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserSearchReq {

    private String name;

    @EnumValid(enumClass = UserSearchType.class, ignoreCase = true)
    private UserSearchType searchType;

    private Integer page = 0;

    private Integer size = 30;

    private Long userId;

    public UserSearchSvcReq toServiceRequest() {
        return UserSearchSvcReq.builder()
                .name(name)
                .userSearchType(searchType)
                .pageable(PageRequest.of(page, size))
                .userId(userId)
                .build();

    }
}
