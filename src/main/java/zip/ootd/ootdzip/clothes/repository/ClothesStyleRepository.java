package zip.ootd.ootdzip.clothes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;

public interface ClothesStyleRepository extends JpaRepository<ClothesStyle, Long> {
}
