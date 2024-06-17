package zip.ootd.ootdzip.userblock.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserBlockGetRes {

    private Long id;
    private Long userId;
    private String userName;
    private String profileImage;

    public static UserBlockGetRes createBy(UserBlock userBlock) {
        return UserBlockGetRes.builder()
                .id(userBlock.getId())
                .userId(userBlock.getBlockedUser().getId())
                .userName(userBlock.getBlockedUser().getName())
                .profileImage(userBlock.getBlockedUser().getImages().getImageUrlMedium())
                .build();
    }
}
