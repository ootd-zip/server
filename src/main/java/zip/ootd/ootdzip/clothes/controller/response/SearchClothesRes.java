package zip.ootd.ootdzip.clothes.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchClothesRes {
    private Long id;
    private String imageUrl;
}
