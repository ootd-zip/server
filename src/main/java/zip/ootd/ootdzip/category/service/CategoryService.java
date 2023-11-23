package zip.ootd.ootdzip.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.CategoryRes;
import zip.ootd.ootdzip.category.data.CategorySearch;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryRes> getCategories(CategorySearch request) {
        //TODO : 페이징 넣을 지 확인 필요
        List<Category> findCategories;

        // 대분류 카테고리 조회 시 모든 대분류 카테고리 조회
        if (request.getCategoryType().equals(CategoryType.LargeCategory)) {
            findCategories = categoryRepository.findCategoriesByType(request.getCategoryType());
            return findCategories.stream()
                    .map(CategoryRes::new)
                    .toList();
        }

        // 중분류 or 소분류 카테고리 조회 시 전달받은 부모 카테고리의 하위 카테고리 조회
        Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ERROR));

        findCategories = categoryRepository.findCategoriesByParentCategoryAndType(parentCategory,
                request.getCategoryType());

        if (findCategories == null || findCategories.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ERROR);
        }

        return findCategories.stream()
                .map(CategoryRes::new)
                .toList();
    }

}
