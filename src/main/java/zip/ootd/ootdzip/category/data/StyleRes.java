package zip.ootd.ootdzip.category.data;

import lombok.Data;
import zip.ootd.ootdzip.category.domain.Style;

@Data
public class StyleRes {

    private Long id;

    private String name;

    public StyleRes(Style style) {
        this.id = style.getId();
        this.name = style.getName();
    }
}
