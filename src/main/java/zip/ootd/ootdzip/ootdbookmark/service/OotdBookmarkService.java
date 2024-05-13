package zip.ootd.ootdzip.ootdbookmark.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkDeleteReq;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkGetAllRes;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.ootdbookmark.repository.OotdBookmarkRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OotdBookmarkService {

    private final OotdBookmarkRepository ootdBookmarkRepository;
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;

    public CommonPageResponse<OotdBookmarkGetAllRes> getOotdBookmarks(User loginUser, CommonPageRequest request) {

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Pageable pageable = request.toPageable();
        Page<OotdBookmark> ootdBookmarks = ootdBookmarkRepository.findAllByUserIdAndWriterIdNotIn(loginUser.getId(),
                nonAccessibleUserIds,
                pageable);
        List<OotdBookmarkGetAllRes> ootdBookmarkGetAllResList = ootdBookmarks.stream()
                .map(OotdBookmarkGetAllRes::of)
                .collect(Collectors.toList());

        return new CommonPageResponse<>(ootdBookmarkGetAllResList,
                pageable,
                ootdBookmarks.isLast(),
                ootdBookmarks.getTotalElements());
    }

    @Transactional
    public void deleteOotdBookmarks(OotdBookmarkDeleteReq request, User loginUser) {

        List<OotdBookmark> ootdBookmarks = ootdBookmarkRepository.findAllByUserAndIdIn(loginUser,
                request.getOotdBookmarkIds());
        ootdBookmarks.forEach(ob -> {
            ob.getOotd().cancelBookmark(ob.getUser());
            ootdBookmarkRepository.deleteById(ob.getId());
        });
    }

}
