package zip.ootd.ootdzip.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.category.data.CategoryRes;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.repository.CategoryRepository;

@Transactional
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @DisplayName("상위 카테고리 ID로 카테고리를 조회한다.")
    @Test
    void getCategories() {
        // given
        Category parentCategory = Category.createLargeCategoryBy("부모카테고리1", SizeType.TOP);
        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category detailCategory = Category.createDetailCategoryBy("하위카테고리1", savedParentCategory, SizeType.TOP);
        Category savedDetailCategory = categoryRepository.save(detailCategory);

        // when
        List<CategoryRes> result = categoryService.getCategories();

        //then
        assertThat(result).hasSize(1)
                .extracting("id", "name")
                .contains(tuple(savedParentCategory.getId(), savedParentCategory.getName()));

        assertThat(result.get(0).getDetailCategories()).hasSize(1)
                .extracting("id", "name")
                .contains(tuple(savedDetailCategory.getId(), savedDetailCategory.getName()));

    }

}
