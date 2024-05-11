package zip.ootd.ootdzip.userblock.repository;

import java.util.Set;

public interface UserBlockRepositoryCustom {

    Boolean existUserBlock(Long userId1, Long userId2);

    Set<Long> getNonAccessibleUserIds(Long accessUserId);
}
