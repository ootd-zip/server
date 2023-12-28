package zip.ootd.ootdzip.clothes.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindClothesByUserReq {

    private Long userId;
}
