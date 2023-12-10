package zip.ootd.ootdzip.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findCategoriesByType(CategoryType type);

    List<Category> findCategoriesByParentCategoryAndType(Category parentCategory, CategoryType type);

}
