package zip.ootd.ootdzip.brand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.brand.domain.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, BrandRepositoryCustom {

    Boolean existsByName(String name);

    Boolean existsByEngName(String engName);

    List<Brand> findByNameStartsWithOrEngNameStartsWith(String name, String engName, Sort sort);

    Optional<Brand> findOneByName(String name);
}
