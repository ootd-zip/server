package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OotdPatchReq {

    @NotNull
    private Boolean isPrivate;
}
