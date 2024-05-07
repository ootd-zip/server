package zip.ootd.ootdzip.userblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
}
