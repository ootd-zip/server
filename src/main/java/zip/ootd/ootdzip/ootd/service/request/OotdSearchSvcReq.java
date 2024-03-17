package zip.ootd.ootdzip.ootd.service.request;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.user.domain.UserGender;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OotdSearchSvcReq {

    private String searchText;
    private List<Long> categoryIds;
    private List<Long> colorIds;
    private List<Long> brandIds;
    private UserGender writerGender;
    private OotdSearchSortType sortType;
    private Pageable pageable;
}
