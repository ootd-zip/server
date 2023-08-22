package zip.ootd.ootdzip.clothes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;

public interface ClothesImageRepository extends JpaRepository<ClothesImage, Long> {
}
