package zip.ootd.ootdzip.ootdbookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkDeleteReq;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkGetAllRes;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.ootdbookmark.repository.OotdBookmarkRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class OotdBookmarkService {

    private final OotdBookmarkRepository ootdBookmarkRepository;
    private final UserService userService;

    public CommonPageResponse<OotdBookmarkGetAllRes> getOotdBookmarks(User loginUser, CommonPageRequest request) {

        Pageable pageable = request.toPageable();
        Page<OotdBookmark> ootdBookmarks = ootdBookmarkRepository.findAllByUserId(loginUser.getId(), pageable);
        List<OotdBookmarkGetAllRes> ootdBookmarkGetAllResList = ootdBookmarks.stream()
                .map(OotdBookmarkGetAllRes::of)
                .collect(Collectors.toList());

        return new CommonPageResponse<>(ootdBookmarkGetAllResList,
                pageable,
                ootdBookmarks.isLast(),
                ootdBookmarks.getTotalElements());
    }

    public void deleteOotdBookmarks(OotdBookmarkDeleteReq request) {

        List<OotdBookmark> ootdBookmarks = ootdBookmarkRepository.findAllById(request.getOotdBookmarkIds());
        ootdBookmarks.forEach(ob -> {
            userService.checkValidUser(ob.getUser());
            ootdBookmarkRepository.deleteById(ob.getId());
        });
    }

}
