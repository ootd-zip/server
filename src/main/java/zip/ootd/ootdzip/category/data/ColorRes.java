package zip.ootd.ootdzip.category.data;

import lombok.Data;
import zip.ootd.ootdzip.category.domain.Color;

@Data
public class ColorRes {

    private Long id;

    private String name;

    private String imageUrl;

    public ColorRes(Color color) {

        this.id = color.getId();
        this.name = color.getName();
        this.imageUrl = color.getImageUrl();
    }
}
