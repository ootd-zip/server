package zip.ootd.ootdzip.s3.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3ImageRes {

    private List<String> imageUrls;
}
