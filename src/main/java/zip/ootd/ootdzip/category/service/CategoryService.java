package zip.ootd.ootdzip.category.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.CategoryRes;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryRes> getCategories() {
        List<Category> findCategories = categoryRepository.findAll();
        List<CategoryRes> result = new ArrayList<>();

        for (Category parentCategory : findCategories.stream()
                .filter(x -> x.getType().equals(CategoryType.LargeCategory))
                .toList()) {
            List<Category> detailCategories = findCategories.stream()
                    .filter(x -> x.getParentCategory() != null
                            && x.getParentCategory().getId().equals(parentCategory.getId()))
                    .toList();

            result.add(CategoryRes.of(parentCategory, detailCategories));
        }

        return result;
    }

}
