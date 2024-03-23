package zip.ootd.ootdzip.ootd.controller.request;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import zip.ootd.ootdzip.common.valid.EnumValid;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.service.request.OotdSearchSvcReq;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class OotdSearchReq {

    private String searchText;

    private List<@Positive(message = "카테고리 ID는 양수여야 합니다.") Long> categoryIds;

    private List<@Positive(message = "색 ID는 양수여야 합니다.") Long> colorIds;

    private List<@Positive(message = "브랜드 ID는 양수여야 합니다.") Long> brandIds;

    private UserGender writerGender;

    @EnumValid(enumClass = OotdSearchSortType.class, ignoreCase = true)
    private OotdSearchSortType sortCriteria;

    private Integer page = 0;

    private Integer size = 30;

    public OotdSearchSvcReq toServiceRequest() {
        return OotdSearchSvcReq.builder()
                .searchText(searchText)
                .categoryIds(categoryIds)
                .colorIds(colorIds)
                .brandIds(brandIds)
                .writerGender(writerGender)
                .sortCriteria(sortCriteria)
                .pageable(PageRequest.of(page, size))
                .build();
    }
}
