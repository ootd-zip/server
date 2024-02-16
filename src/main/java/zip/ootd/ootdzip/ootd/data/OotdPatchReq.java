package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OotdPatchReq {

    @NotNull
    private Long id;

    @Size(max = 3000, message = "메모는 최대 3000자 입니다.")
    private String content;

    @NotNull
    private Boolean isPrivate;
}
