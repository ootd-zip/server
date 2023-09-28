package zip.ootd.ootdzip.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import zip.ootd.ootdzip.category.data.DetailCategoryDto;
import zip.ootd.ootdzip.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.id, c.name as categoryName, mc.name as middleCategoryName, lc.name as largeCategoryName "
            + "FROM Category c " + "INNER JOIN Category mc ON c.parentCategory = mc "
            + "INNER JOIN Category lc ON mc.parentCategory = lc " + "WHERE c.id = :id")
    DetailCategoryDto findDetailCategoryById(Long id);
}
