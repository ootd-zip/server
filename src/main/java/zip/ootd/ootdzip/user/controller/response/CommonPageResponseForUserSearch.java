package zip.ootd.ootdzip.user.controller.response;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import zip.ootd.ootdzip.common.response.CommonPageResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonPageResponseForUserSearch<T> extends CommonPageResponse<T> {

    Long followerCount;
    Long followingCount;

    public CommonPageResponseForUserSearch(List<T> content, Pageable pageable, Boolean isLast, Long total,
            Long followerCount, Long followingCount) {

        super(content, pageable, isLast, total);

        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }
}
