package zip.ootd.ootdzip.user.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnfollowReq {
    @NotNull
    private long userId;
}
