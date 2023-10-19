package zip.ootd.ootdzip.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.category.domain.Style;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {
}
