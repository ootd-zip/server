package zip.ootd.ootdzip.ootdlike.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.ootdlike.controller.response.OotdLikeRes;
import zip.ootd.ootdzip.ootdlike.domain.OotdLike;
import zip.ootd.ootdzip.ootdlike.repository.OotdLikeRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OotdLikeService {

    private final OotdLikeRepository ootdLikeRepository;
    private final UserBlockRepository userBlockRepository;

    public List<OotdLikeRes> getUserOotdLikes(User loginUser) {

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        List<OotdLike> ootdLikes = ootdLikeRepository.findTop10ByUserAndWriterIdNotIn(loginUser.getId(),
                nonAccessibleUserIds,
                PageRequest.of(0, 10, sortByCreatedAt(Direction.DESC)));

        return ootdLikes.stream()
                .map(OotdLikeRes::of)
                .toList();
    }

    private Sort sortByCreatedAt(Direction direction) {
        return Sort.by(direction, "createdAt");
    }

}
