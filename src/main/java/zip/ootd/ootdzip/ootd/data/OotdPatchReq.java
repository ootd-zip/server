package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OotdPatchReq {

    @NotNull
    private Long id;

    private String content;

    @NotNull
    private Boolean isPrivate;
}
