package zip.ootd.ootdzip.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.SizeReq;
import zip.ootd.ootdzip.category.data.SizeRes;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;

    public List<SizeRes> findByCategory(SizeReq request) {
        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID"));

        List<Size> sizes = sizeRepository.findBySizeType(findCategory.getSizeType());

        if (sizes == null || sizes.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_REGISTERED_SIZE);
        }

        return sizes.stream()
                .map(SizeRes::of)
                .toList();
    }
}
