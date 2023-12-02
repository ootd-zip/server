package zip.ootd.ootdzip.category.data;

import lombok.Data;
import zip.ootd.ootdzip.category.domain.Size;

@Data
public class SizeRes {

    private Long id;

    private String name;

    public SizeRes(Size size) {

        this.id = size.getId();
        this.name = size.getName();
    }
}
