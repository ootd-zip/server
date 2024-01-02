package zip.ootd.ootdzip.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

