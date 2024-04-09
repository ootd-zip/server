package zip.ootd.ootdzip.ootdlike.service;

import java.util.List;

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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OotdLikeService {

    private final OotdLikeRepository ootdLikeRepository;

    public List<OotdLikeRes> getUserOotdLikes(User loginUser) {

        List<OotdLike> ootdLikes = ootdLikeRepository.findTop10ByUser(loginUser.getId(),
                PageRequest.of(0, 10, sortByCreatedAt(Direction.DESC)));

        return ootdLikes.stream()
                .map(OotdLikeRes::of)
                .toList();
    }

    private Sort sortByCreatedAt(Direction direction) {
        return Sort.by(direction, "createdAt");
    }

}
