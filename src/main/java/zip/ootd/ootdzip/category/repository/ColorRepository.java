package zip.ootd.ootdzip.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.category.domain.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
}
