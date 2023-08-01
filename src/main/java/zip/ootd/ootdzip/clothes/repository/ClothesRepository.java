package zip.ootd.ootdzip.clothes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long> {
}
