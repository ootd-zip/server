package zip.ootd.ootdzip.clothes.service.request;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SearchClothesSvcReq {

    private final Long userId;

    private Boolean isPrivate;

    private List<Long> brandIds;

    private List<Long> categoryIds;

    private List<Long> colorIds;

    private final Pageable pageable;
}
