package zip.ootd.ootdzip.clothes.controller.request;

import java.util.List;

import jakarta.validation.constraints.Positive;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.service.request.SearchClothesSvcReq;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Setter
public class SearchClothesReq extends CommonPageRequest {

    @Positive(message = "유저 ID는 양수여야 합니다.")
    private Long userId;

    private Boolean isPrivate;

    private List<@Positive(message = "브랜드 ID는 양수여야 합니다.") Long> brandIds;

    private List<@Positive(message = "카테고리 ID는 양수여야 합니다.") Long> categoryIds;

    private List<@Positive(message = "색 ID는 양수여야 합니다.") Long> colorIds;

    private String searchText;

    public SearchClothesSvcReq toServiceRequest() {
        return SearchClothesSvcReq.builder()
                .userId(this.userId)
                .isPrivate(isPrivate)
                .brandIds(brandIds)
                .categoryIds(categoryIds)
                .colorIds(colorIds)
                .searchText(searchText)
                .pageable(this.toPageable())
                .build();
    }
}
