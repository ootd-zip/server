package zip.ootd.ootdzip.brandrequest.controller.reqeuest;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSearchSvcReq;
import zip.ootd.ootdzip.common.request.SortColumn;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BrandRequestSearchReq {

    private BrandRequestStatus searchStatus;
    private String searchText;
    private Integer pageNo;
    private Integer pageSize;
    private List<SortColumn> sortColumns;

    public BrandRequestSearchSvcReq toServiceRequest() {
        Sort sort = Sort.unsorted();
        sortColumns.stream().forEach((sortColumn -> {
            sort.and(sortColumn.toSort());
        }));

        return BrandRequestSearchSvcReq.builder()
                .searchStatus(searchStatus)
                .searchText(searchText)
                .pageable(PageRequest.of(pageNo, pageSize, sort))
                .build();
    }
}
