package zip.ootd.ootdzip.user.service.request;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.data.UserSearchType;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserSearchSvcReq {
    private String name;
    private UserSearchType userSearchType;
    private Pageable pageable;
}
