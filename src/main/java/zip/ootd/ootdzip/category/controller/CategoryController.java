package zip.ootd.ootdzip.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.CategoryRes;
import zip.ootd.ootdzip.category.data.CategorySearch;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.service.CategoryService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category 컨트롤러", description = "카테고리 관련 API입니다.")
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ApiResponse<List<CategoryRes>> getCategories(@RequestParam CategoryType categoryType,
            @RequestParam Long parentCategoryId) {
        return new ApiResponse<>(categoryService.getCategories(new CategorySearch(categoryType, parentCategoryId)));
    }
}
