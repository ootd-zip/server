package zip.ootd.ootdzip.userblock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.userblock.domain.UserBlock;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;
import zip.ootd.ootdzip.userblock.service.request.BlockUserSvcReq;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    @Transactional
    public void blockUser(BlockUserSvcReq request, User loginUser) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_ID));

        if (targetUser.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER_ERROR);
        }

        UserBlock userBlock = UserBlock.createBy(targetUser, loginUser);

        userBlockRepository.save(userBlock);
    }

}
