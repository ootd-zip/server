package zip.ootd.ootdzip.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    List<Size> findBySizeType(SizeType sizeType);
}
