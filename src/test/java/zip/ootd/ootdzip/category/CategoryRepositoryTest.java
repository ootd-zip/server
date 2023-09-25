package zip.ootd.ootdzip.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.repository.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void 세부카테고리조회_성공() {
        //Given(준비)
        Category largeCategory = Category
                .builder()
                .name("카테고리1")
                .type(CategoryType.LargeCategory)
                .build();

        Category savedLargeCategory = categoryRepository.save(largeCategory);

        Category middleCategory = Category
                .builder()
                .name("카테고리2")
                .type(CategoryType.MiddleCategory)
                .parentCategory(savedLargeCategory)
                .build();

        Category savedMiddleCategory = categoryRepository.save(middleCategory);

        Category detailCategory = Category
                .builder()
                .name("카테고리3")
                .type(CategoryType.DetailCategory)
                .parentCategory(savedMiddleCategory)
                .build();

        Category savedDetailCategory = categoryRepository.save(detailCategory);
        //When(실행)
        DetailCategory result = categoryRepository.findDetailCategoryById(savedDetailCategory.getId());
        //Then(검증)
        assertThat(result.getCategoryName()).isEqualTo(savedDetailCategory.getName());
        assertThat(result.getLargeCategoryName()).isEqualTo(savedLargeCategory.getName());
        assertThat(result.getMiddleCategoryName()).isEqualTo(savedMiddleCategory.getName());
    }
}
