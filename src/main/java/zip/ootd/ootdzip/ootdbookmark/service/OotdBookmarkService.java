package zip.ootd.ootdzip.ootdbookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkDeleteReq;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkGetAllRes;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.ootdbookmark.repository.OotdBookmarkRepository;
import zip.ootd.ootdzip.user.domain.User;

@Service
@Transactional
@RequiredArgsConstructor
public class OotdBookmarkService {

    private final OotdBookmarkRepository ootdBookmarkRepository;

    public CommonSliceResponse<OotdBookmarkGetAllRes> getOotdBookmarks(User loginUser, CommonPageRequest request) {

        Pageable pageable = request.toPageable();
        Slice<OotdBookmark> ootdBookmarks = ootdBookmarkRepository.findAllByUserId(loginUser.getId(), pageable);
        List<OotdBookmarkGetAllRes> ootdBookmarkGetAllResList = ootdBookmarks.stream()
                .map(OotdBookmarkGetAllRes::of)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdBookmarkGetAllResList, pageable, ootdBookmarks.isLast());
    }

    public void deleteOotdBookmarks(OotdBookmarkDeleteReq request) {

        ootdBookmarkRepository.deleteAllById(request.getOotdBookmarkIds());
    }

}
