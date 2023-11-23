package zip.ootd.ootdzip.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT new zip.ootd.ootdzip.category.data.DetailCategory(c.id, c.name, mc.name, lc.name)"
            + "FROM Category c " + "INNER JOIN Category mc ON c.parentCategory = mc "
            + "INNER JOIN Category lc ON mc.parentCategory = lc " + "WHERE c.id = :id")
    DetailCategory findDetailCategoryById(Long id);

    List<Category> findCategoriesByType(CategoryType type);

    List<Category> findCategoriesByParentCategoryAndType(Category parentCategory, CategoryType type);

}
