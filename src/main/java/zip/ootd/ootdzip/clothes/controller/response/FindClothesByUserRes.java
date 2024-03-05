package zip.ootd.ootdzip.clothes.controller.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FindClothesByUserRes {
    private Long id;
    private String imageUrl;
}
