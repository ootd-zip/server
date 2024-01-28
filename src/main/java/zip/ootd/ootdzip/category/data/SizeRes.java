package zip.ootd.ootdzip.category.data;

import lombok.Builder;
import lombok.Getter;
import zip.ootd.ootdzip.category.domain.Size;

@Getter
public class SizeRes {

    private final Long id;

    private final String name;

    private final Byte lineNo;

    @Builder
    private SizeRes(Long id, String name, Byte lineNo) {
        this.id = id;
        this.name = name;
        this.lineNo = lineNo;
    }

    public static SizeRes of(Size size) {
        return SizeRes.builder()
                .id(size.getId())
                .name(size.getName())
                .lineNo(size.getLineNo())
                .build();
    }
}
