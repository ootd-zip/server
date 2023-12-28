package zip.ootd.ootdzip.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.brand.domain.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Boolean existsByName(String name);
}
