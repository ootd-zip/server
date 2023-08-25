package zip.ootd.ootdzip.s3.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class S3ImageRes {

    private List<String> ootdImageUrls;
}
